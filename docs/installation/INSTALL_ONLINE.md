# MAS 9.2 설치 가이드 — 온라인 환경

> **상태**: 검토 반영본  
> 인터넷 연결이 가능한 환경 기준입니다. 폐쇄망 환경은 [INSTALL_OFFLINE.md](INSTALL_OFFLINE.md)를 참고합니다.

---

## 목차

- [1. 기준 및 전제](#1-기준-및-전제)
- [2. 사전 준비](#2-사전-준비)
- [3. OCP SNO 설치](#3-ocp-sno-설치)
- [4. 스토리지 및 Registry 구성](#4-스토리지-및-registry-구성)
- [5. MAS 9.2 설치](#5-mas-92-설치)
- [6. IT 모듈 활성화](#6-it-모듈-활성화)
- [7. BIRT 구성](#7-birt-구성)
- [8. 설치 후 점검](#8-설치-후-점검)

---

## 1. 기준 및 전제

이 문서는 다음 버전 조합을 기준으로 작성합니다.

| 구성 요소 | 기준값 |
|-----------|--------|
| OpenShift | `4.16.x` |
| IBM Maximo Operator Catalog | `v9-250828-amd64` |
| MAS CLI | `quay.io/ibmmas/cli:15.2.0` |
| MAS / Manage Channel | `9.2` |

> `quay.io/ibmmas/cli:latest`는 사용하지 않습니다. catalog `v9-250828-amd64`를 사용할 때는 같은 시기의 MAS CLI 버전으로 고정해야 합니다.
>
> ⚠️ 미검증
>
> `quay.io/ibmmas/cli:15.2.0`은 `v9-250828-amd64` catalog와 같은 시기의 CLI로 정리한 값입니다. 실제 설치 전 IBM MAS CLI catalog 문서와 `mas install --help` 출력으로 조합을 재확인합니다.

설치 방식은 MAS CLI가 MAS Core, Manage, SLS, MongoDB, DB2 등 필요한 의존성을 설치/구성하는 흐름을 기본으로 합니다.

> ⚠️ 미검증
>
> DB2 또는 MongoDB를 사전에 수동으로 생성해서 MAS에 연결하는 방식은 별도 `JdbcCfg`, `MongoCfg` 구성 검증이 필요합니다. 이 문서의 기본 절차에서는 수동 DB2/MongoDB 생성 절차를 사용하지 않습니다.

---

## 2. 사전 준비

### 2.1 서버 구성

| 구분 | OS / 상태 | 역할 | 최소 사양 |
|------|-----------|------|-----------|
| Bastion | RHEL 9.x 권장 | ISO 생성, MAS CLI, oc CLI, DNS | 4 core / 8 GB RAM / 100 GB |
| SNO 노드 | OS 미설치 VM 또는 물리 서버 | OCP 컨트롤 플레인 + 워커 통합 | 16 core / 64 GB RAM / OS 300 GB + 데이터 500 GB |

Bastion은 설치 도구와 컨테이너 런타임을 안정적으로 사용하기 위해 RHEL 계열을 권장합니다. 현장 표준이 있으면 RHEL 8/9 또는 호환 배포판을 사용할 수 있지만, 패키지 설치 명령은 OS에 맞게 조정해야 합니다.

SNO 노드는 사전에 RHEL 같은 일반 OS를 설치하지 않습니다. Assisted Installer에서 생성한 Discovery ISO로 부팅하면 설치 과정에서 RHCOS(Red Hat CoreOS)와 OpenShift가 노드에 설치됩니다. VM으로 구성하는 경우에도 “빈 VM + ISO 부팅” 형태로 준비합니다.

SNO 노드는 디스크 2개 구성을 권장합니다. 첫 번째 디스크는 RHCOS/OpenShift용, 두 번째 디스크는 LVM Storage용으로 사용합니다.

### 2.2 필수 파일

| 파일 | 용도 |
|------|------|
| IBM Entitlement Key | IBM 이미지 pull 인증 |
| `entitlement.lic` | SLS AppPoints 라이선스 |
| Red Hat Pull Secret | OpenShift 설치 |
| SSH public key | Assisted Installer 노드 접근 |

### 2.3 DNS 구성

예시 값은 다음과 같습니다.

| 항목 | 예시 |
|------|------|
| Cluster name | `<cluster-name>` |
| Base domain | `<base-domain>` |
| SNO IP | `<sno-ip>` |
| Bastion IP | `<bastion-ip>` |

`/etc/dnsmasq.d/mas.conf` 예시:

```ini
server=<upstream-dns-ip>

address=/api.<cluster-name>.<base-domain>/<sno-ip>
address=/api-int.<cluster-name>.<base-domain>/<sno-ip>
address=/.apps.<cluster-name>.<base-domain>/<sno-ip>
```

서비스 활성화:

```bash
dnf install -y dnsmasq
systemctl enable --now dnsmasq
firewall-cmd --permanent --add-service=dns
firewall-cmd --reload
```

DNS 확인:

```bash
nslookup api.<cluster-name>.<base-domain>
nslookup api-int.<cluster-name>.<base-domain>
nslookup test.apps.<cluster-name>.<base-domain>
```

3개 모두 `<sno-ip>`로 응답해야 합니다.

### 2.4 Bastion CLI 준비

```bash
curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.16/openshift-client-linux.tar.gz
tar -xvf openshift-client-linux.tar.gz
mv oc kubectl /usr/local/bin/

dnf config-manager --add-repo https://download.docker.com/linux/rhel/docker-ce.repo
dnf install -y docker-ce docker-ce-cli
systemctl enable --now docker

ssh-keygen -t rsa -b 4096 -f ~/.ssh/id_rsa -N ""
```

MAS CLI 버전 확인:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  quay.io/ibmmas/cli:15.2.0 mas install --help
```

---

## 3. OCP SNO 설치

### 3.1 Assisted Installer에서 클러스터 생성

1. Red Hat Hybrid Cloud Console 접속
2. **Create cluster → Datacenter → Assisted Installer**
3. 아래 값을 입력합니다.

| 항목 | 값 |
|------|-----|
| Cluster name | `<cluster-name>` |
| Base domain | `<base-domain>` |
| OpenShift version | `4.16.x` |
| Single Node OpenShift | 선택 |
| Network | Static IP 권장 |
| SSH public key | `~/.ssh/id_rsa.pub` 내용 |

### 3.2 Discovery ISO 부팅 및 설치

1. Discovery ISO 다운로드
2. SNO 노드에 ISO 마운트 후 부팅
3. Assisted Installer 콘솔에서 노드 감지 확인
4. 설치 조건이 충족되면 **Install cluster** 실행

설치 완료 후 kubeconfig를 Bastion에 저장합니다.

```bash
mkdir -p ~/.kube
cp <kubeconfig-path> ~/.kube/config

oc get nodes
oc get clusterversion
oc get co
```

모든 Cluster Operator가 `AVAILABLE=True`, `PROGRESSING=False`, `DEGRADED=False` 상태여야 합니다.

---

## 4. 스토리지 및 Registry 구성

### 4.1 LVM Operator 설치

LVM Storage는 RWO 용도로만 사용합니다.

```bash
oc new-project openshift-storage

oc apply -f - <<EOF
apiVersion: operators.coreos.com/v1alpha1
kind: Subscription
metadata:
  name: lvms-operator
  namespace: openshift-storage
spec:
  installPlanApproval: Automatic
  name: lvms-operator
  source: redhat-operators
  sourceNamespace: openshift-marketplace
EOF
```

디스크 확인:

```bash
oc debug node/<node-name> -- chroot /host lsblk
```

LVMCluster 생성:

```bash
oc apply -f - <<EOF
apiVersion: lvm.topolvm.io/v1alpha1
kind: LVMCluster
metadata:
  name: lvmcluster
  namespace: openshift-storage
spec:
  storage:
    deviceClasses:
      - name: vg1
        deviceSelector:
          paths:
            - /dev/<data-disk>
        thinPoolConfig:
          name: thin-pool-1
          sizePercent: 90
          overprovisionRatio: 10
EOF
```

StorageClass 확인:

```bash
oc get storageclass
```

> `odf-lvm-vg1` 같은 LVM StorageClass는 `ReadWriteOnce` 용도로만 사용합니다. `Storage Class (RWX)`에는 별도의 RWX 지원 StorageClass를 입력해야 합니다.

### 4.2 RWX Storage 준비

MAS/Manage 구성에 RWX PVC가 필요한 경우 별도 RWX StorageClass를 준비합니다.

| 구분 | 예시 |
|------|------|
| RWO StorageClass | `<rwo-storage-class>` |
| RWX StorageClass | `<rwx-storage-class>` |

> ⚠️ 미검증
>
> 현재 실습 환경에 RWX StorageClass가 없다면 MAS 설치 프롬프트에서 어떤 항목이 RWX를 요구하는지 실제 `mas install` 프롬프트와 설치 로그로 확인해야 합니다. LVM StorageClass를 RWX 값으로 대체하지 않습니다.

### 4.3 Image Registry 구성

SNO 개발/검증 환경에서는 OpenShift Image Registry를 `emptyDir`로 구성할 수 있습니다.

```bash
oc patch configs.imageregistry.operator.openshift.io cluster \
  --type merge \
  --patch '{"spec":{"managementState":"Managed","storage":{"emptyDir":{}}}}'
```

> `emptyDir` registry storage는 운영용 권장 구성이 아닙니다. 노드 재시작/장애 시 registry 데이터가 보존되지 않을 수 있습니다.

---

## 5. MAS 9.2 설치

### 5.1 IBM Maximo Operator Catalog 등록

```bash
oc apply -f - <<EOF
apiVersion: operators.coreos.com/v1alpha1
kind: CatalogSource
metadata:
  name: ibm-maximo-operator-catalog
  namespace: openshift-marketplace
spec:
  displayName: IBM Maximo Operator Catalog
  image: icr.io/cpopen/ibm-maximo-operator-catalog:v9-250828-amd64
  publisher: IBM
  sourceType: grpc
  updateStrategy:
    registryPoll:
      interval: 45m
EOF
```

READY 확인:

```bash
oc get catalogsource -n openshift-marketplace ibm-maximo-operator-catalog
oc get packagemanifest -n openshift-marketplace | grep -i maximo
```

### 5.2 MAS CLI 실행

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v ~/.kube:/root/.kube \
  quay.io/ibmmas/cli:15.2.0 mas install
```

대화형 프롬프트 기준 입력값:

| 항목 | 값 |
|------|-----|
| IBM Entitlement Key | `<ibm-entitlement-key>` |
| MAS License File | `/mnt/home/<entitlement-file>` |
| Catalog Source | `ibm-maximo-operator-catalog` |
| Subscription Channel | `9.2` |
| MAS Instance ID | `<mas-instance-id>` |
| Workspace ID | `<workspace-id>` |
| Storage Class (RWO) | `<rwo-storage-class>` |
| Storage Class (RWX) | `<rwx-storage-class>` |

> 프롬프트 문구는 MAS CLI 버전에 따라 다를 수 있습니다. 이 문서는 `quay.io/ibmmas/cli:15.2.0` 기준으로 catalog를 고정합니다.

### 5.3 설치 상태 확인

```bash
oc get suite -A
oc get subscriptions -A
oc get installplan -A
oc get pods -A | grep -v Running | grep -v Completed | grep -v Succeeded
```

MAS 네임스페이스 예시:

```bash
oc get pods -n mas-<mas-instance-id>-core
oc get events -n mas-<mas-instance-id>-core --sort-by=.lastTimestamp
```

> ⚠️ 미검증
>
> Operator pod label은 catalog/버전에 따라 달라질 수 있습니다. 로그 확인 시 `oc get pods -n mas-<mas-instance-id>-core`로 실제 pod 이름을 먼저 확인한 뒤 `oc logs`를 실행합니다.

---

## 6. IT 모듈 활성화

### 6.1 Suite Administration에서 활성화

MAS Admin URL 예시:

```text
https://admin.<mas-instance-id>.apps.<cluster-name>.<base-domain>
```

진행 순서:

1. Suite Administration 로그인
2. Catalog 또는 Applications 메뉴에서 Manage 확인
3. IT 관련 Industry Solution 또는 Add-on 활성화
4. Workspace `<workspace-id>`에 적용

> ⚠️ 미검증
>
> 메뉴 명칭은 MAS/Manage fix pack에 따라 달라질 수 있습니다. 실제 UI에서 `Manage`, `IT`, `Industry Solution`, `Workspace` 항목을 기준으로 확인합니다.

### 6.2 DB 초기화 스크립트

기존 초안에는 아래 명령이 포함되어 있었지만, 현재 문서에서는 자동 실행 여부가 확인되지 않았으므로 운영 절차로 단정하지 않습니다.

> ⚠️ 미검증
>
> ```bash
> oc exec -it -n mas-<mas-instance-id>-manage <manage-pod> -- bash
> cd /opt/IBM/SMP/maximo/tools/maximo
> ./updatedb.sh
> ./runscriptfile.sh -cIT -f SETUPIT
> ```
>
> 위 명령은 실제 Manage pod 경로, 컨테이너명, IT solution 적용 방식, MAS CLI 자동 실행 여부를 확인한 뒤 사용합니다.

---

## 7. BIRT 구성

MAS Manage의 보고서 기능은 Manage 배포 상태와 라이선스/애플리케이션 구성에 따라 달라집니다. 기본 설치 후 먼저 Manage UI에서 보고서 메뉴가 활성화되는지 확인합니다.

확인 항목:

- Manage 애플리케이션이 정상 기동했는지 확인
- 보고서 메뉴 접근 가능 여부 확인
- 샘플 보고서 실행 가능 여부 확인

> ⚠️ 미검증
>
> 기존 초안의 `oc patch manageapp ... birt latest` 명령과 `mxe.report.birt.viewerurl=http://localhost:9080/...` 값은 현재 공식 절차와 실제 CR 이름 확인 전까지 운영 절차로 사용하지 않습니다.
>
> ```bash
> oc patch manageapp <manageapp-name> \
>   -n mas-<mas-instance-id>-manage \
>   --type merge \
>   -p '<patch-json>'
> ```

---

## 8. 설치 후 점검

```bash
oc get nodes
oc get co
oc get suite -A
oc get manageapp -A
oc get pods -A | grep -v Running | grep -v Completed | grep -v Succeeded
```

브라우저 확인:

| 항목 | URL 예시 |
|------|----------|
| OpenShift Console | `https://console-openshift-console.apps.<cluster-name>.<base-domain>` |
| MAS Admin | `https://admin.<mas-instance-id>.apps.<cluster-name>.<base-domain>` |
| MAS Manage | 실제 Route를 `oc get route -A`로 확인 |

Route 확인:

```bash
oc get route -A | grep -E 'mas|manage'
```
