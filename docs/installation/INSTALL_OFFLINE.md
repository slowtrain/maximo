# MAS 9.2 설치 가이드 — 오프라인 환경

> **상태**: 검토 반영본
> 인터넷 연결이 불가능한 폐쇄망 환경 기준입니다. 온라인 환경은 [INSTALL_ONLINE.md](INSTALL_ONLINE.md)를 참고합니다.

---

## 목차

- [1. 기준 및 전제](#1-기준-및-전제)
- [2. 사전 준비](#2-사전-준비)
- [3. Mirror Registry 구성](#3-mirror-registry-구성)
- [4. 이미지 미러링](#4-이미지-미러링)
- [5. OCP SNO 설치](#5-ocp-sno-설치)
- [6. Airgap 구성](#6-airgap-구성)
- [7. MAS 9.2 설치](#7-mas-92-설치)
- [8. 설치 후 확인](#8-설치-후-확인)

---

## 1. 기준 및 전제

이 문서는 온라인 문서와 동일하게 다음 버전 조합을 기준으로 합니다.

| 구성 요소                   | 기준값                        |
| --------------------------- | ----------------------------- |
| OpenShift                   | `4.16.x`                    |
| IBM Maximo Operator Catalog | `v9-250828-amd64`           |
| MAS CLI                     | `quay.io/ibmmas/cli:15.2.0` |
| MAS / Manage Channel        | `9.2`                       |

> 오프라인 설치에서는 버전 핀닝이 특히 중요합니다. MAS CLI, catalog, OpenShift, mirror 결과물을 같은 기준으로 고정하지 않으면 폐쇄망에서 재현이 어렵습니다.
>
> ⚠️ 미검증
>
> `quay.io/ibmmas/cli:15.2.0`은 `v9-250828-amd64` catalog와 같은 시기의 CLI로 정리한 값입니다. 실제 설치 전 `mas install --help`, `mas mirror-images --help`, IBM MAS CLI catalog 페이지에서 이 조합을 다시 확인합니다.

폐쇄망 이미지는 두 단계로 이동합니다.

```text
[ 인터넷 PC ]
  이미지 다운로드 / 파일시스템 저장
        |
        | 이동식 디스크 또는 파일 전송
        v
[ Bastion ]
  Mirror Registry로 Push
        |
        v
[ OCP SNO ]
  Mirror Registry에서 이미지 Pull
```

---

## 2. 사전 준비

### 2.1 서버 구성

| 구분      | 역할                                  | 최소 사양                                       |
| --------- | ------------------------------------- | ----------------------------------------------- |
| 인터넷 PC | 이미지 다운로드 및 파일시스템 저장    | Docker 또는 Podman, 여유 디스크 1 TB 이상       |
| Bastion   | Mirror Registry, DNS, MAS CLI, oc CLI | 4 core / 16 GB RAM / 1 TB 이상                  |
| SNO 노드  | OpenShift SNO                         | 16 core / 64 GB RAM / OS 300 GB + 데이터 500 GB |

### 2.2 인터넷 PC에서 확보할 파일

폐쇄망 Bastion은 인터넷에 직접 접속하지 못하므로, 아래 파일은 인터넷 PC에서 먼저 확보한 뒤 이동식 디스크 또는 내부 파일 전송 방식으로 Bastion에 반입합니다.

#### 2.2.1 IBM Entitlement Key

IBM Container Registry(`icr.io`)에서 MAS, DB2, SLS 등 IBM 이미지를 Pull할 때 사용하는 인증 키입니다. 문자열 형태이며 문서나 스크립트에 실제 값을 직접 남기지 않습니다.

획득 방법:

1. IBM Container Library 접속: [https://myibm.ibm.com/products-services/containerlibrary](https://myibm.ibm.com/products-services/containerlibrary)
2. IBM 계정으로 로그인
3. **Entitlement keys** 메뉴에서 key 복사
4. 인터넷 PC의 안전한 임시 파일에 저장

예시:

```bash
read -s IBM_ENTITLEMENT_KEY
```

> 실제 key 값은 문서에 기록하지 않습니다. 필요한 경우 설치 실행 시 환경 변수나 프롬프트로만 입력합니다.

#### 2.2.2 `entitlement.lic`

MAS SLS(Suite License Service)에 등록할 AppPoints 라이선스 파일입니다. 이 파일이 없으면 MAS 설치 또는 애플리케이션 활성화가 진행되지 않습니다.

획득 방법:

1. IBM License Key Center 접속: [https://licensing.subscribenet.com/control/ibmr/login](https://licensing.subscribenet.com/control/ibmr/login)
2. IBM 계정으로 로그인
3. 계약/제품 목록에서 Maximo Application Suite 또는 AppPoints 관련 항목 선택
4. 라이선스 파일 다운로드
5. 파일명을 식별 가능한 이름으로 저장

예시:

```bash
mkdir -p ~/mas-install/licenses
cp <downloaded-license-file> ~/mas-install/licenses/entitlement.lic
```

#### 2.2.3 Red Hat Pull Secret

OpenShift 설치와 Red Hat 이미지 Pull에 사용하는 인증 JSON 파일입니다.

획득 방법:

1. Red Hat Hybrid Cloud Console 접속: [https://console.redhat.com/openshift/install](https://console.redhat.com/openshift/install)
2. Red Hat 계정으로 로그인
3. **Pull secret** 다운로드
4. 파일명을 `pull-secret.txt` 또는 `pull-secret.json`으로 저장

예시:

```bash
mkdir -p ~/mas-install/redhat
cp <downloaded-pull-secret> ~/mas-install/redhat/pull-secret.txt
```

#### 2.2.4 OpenShift CLI — `openshift-client-linux.tar.gz`

`oc`, `kubectl` 명령을 제공하는 OpenShift CLI 패키지입니다. Bastion에서 클러스터 설치 확인과 운영 작업에 사용합니다.

획득 방법:

1. OpenShift client mirror 접속: [https://mirror.openshift.com/pub/openshift-v4/clients/ocp/](https://mirror.openshift.com/pub/openshift-v4/clients/ocp/)
2. 설치할 OpenShift 버전 디렉터리 선택
3. `openshift-client-linux.tar.gz` 다운로드

예시:

```bash
mkdir -p ~/mas-install/ocp
cd ~/mas-install/ocp

curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.16.0/openshift-client-linux.tar.gz
```

> OpenShift patch 버전은 실제 설치 버전에 맞춥니다. 예: `4.16.0`, `4.16.20` 등

#### 2.2.5 OpenShift Installer — `openshift-install-linux.tar.gz`

Agent-based installer ISO 생성과 설치 진행 상태 확인에 사용하는 OpenShift 설치 도구입니다.

획득 방법:

1. OpenShift client mirror 접속: [https://mirror.openshift.com/pub/openshift-v4/clients/ocp/](https://mirror.openshift.com/pub/openshift-v4/clients/ocp/)
2. 설치할 OpenShift 버전 디렉터리 선택
3. `openshift-install-linux.tar.gz` 다운로드

예시:

```bash
cd ~/mas-install/ocp

curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.16.0/openshift-install-linux.tar.gz
```

#### 2.2.6 `oc-mirror.tar.gz`

OpenShift 및 Operator 이미지를 Mirror Registry로 미러링할 때 사용하는 `oc mirror` 플러그인입니다. MAS CLI의 mirror 명령 내부에서도 관련 기능을 사용할 수 있습니다.

획득 방법:

1. OpenShift client mirror 접속: [https://mirror.openshift.com/pub/openshift-v4/clients/ocp/](https://mirror.openshift.com/pub/openshift-v4/clients/ocp/)
2. 설치할 OpenShift 버전 디렉터리 선택
3. `oc-mirror.tar.gz` 다운로드

예시:

```bash
cd ~/mas-install/ocp

curl -LO https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.16.0/oc-mirror.tar.gz
```

#### 2.2.7 MAS CLI 이미지

MAS 설치, 이미지 미러링, airgap 설정, registry setup을 수행하는 CLI 컨테이너 이미지입니다. 이 문서는 `v9-250828-amd64` catalog 기준이므로 MAS CLI도 `15.2.0`으로 고정합니다.

획득 방법:

1. 인터넷 PC에서 `quay.io/ibmmas/cli:15.2.0` 이미지 Pull
2. `docker save`로 tar 파일 생성
3. tar 파일을 Bastion으로 반입

예시:

```bash
mkdir -p ~/mas-install/images

docker pull quay.io/ibmmas/cli:15.2.0
docker save quay.io/ibmmas/cli:15.2.0 \
  -o ~/mas-install/images/mas-cli-15.2.0.tar
```

#### 2.2.8 Docker 또는 Podman 오프라인 설치 패키지

Bastion에서 MAS CLI 컨테이너를 실행하려면 Docker 또는 Podman이 필요합니다. Bastion이 인터넷에 접속할 수 없다면 인터넷 PC에서 RPM과 의존 패키지를 먼저 받아야 합니다.

획득 방법:

1. 인터넷 PC에 Docker CE 저장소 추가
2. `dnf download --resolve`로 RPM 및 의존 패키지 다운로드
3. 다운로드한 디렉터리를 Bastion으로 반입

예시:

```bash
mkdir -p ~/mas-install/docker-rpms

dnf config-manager --add-repo https://download.docker.com/linux/rhel/docker-ce.repo
dnf download --resolve --destdir=~/mas-install/docker-rpms \
  docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

> ⚠️ 미검증
>
> Bastion OS가 RHEL이 아닌 경우 Docker/Podman 패키지 확보 방법이 달라질 수 있습니다. 현장 표준 런타임이 Podman이면 사내 표준 RPM 저장소 또는 Red Hat Subscription 기준으로 패키지를 확보합니다.

#### 2.2.9 Bastion으로 파일 반입

인터넷 PC에서 준비한 파일을 Bastion으로 복사합니다. 폐쇄망 정책에 따라 이동식 디스크, 내부 파일 서버, SCP 중 허용된 방식을 사용합니다.

예시:

```bash
rsync -avh ~/mas-install/ <bastion-user>@<bastion-ip>:/home/<bastion-user>/mas-install/
```

Bastion 반입 후 예상 디렉터리:

```text
/home/<bastion-user>/mas-install/
  licenses/
    entitlement.lic
  redhat/
    pull-secret.txt
  ocp/
    openshift-client-linux.tar.gz
    openshift-install-linux.tar.gz
    oc-mirror.tar.gz
  images/
    mas-cli-15.2.0.tar
  docker-rpms/
    *.rpm
```

### 2.3 Bastion DNS 구성

`/etc/dnsmasq.d/mas.conf` 예시:

```ini
server=<upstream-dns-ip>

address=/api.<cluster-name>.<base-domain>/<sno-ip>
address=/api-int.<cluster-name>.<base-domain>/<sno-ip>
address=/.apps.<cluster-name>.<base-domain>/<sno-ip>
address=/registry.<cluster-name>.<base-domain>/<bastion-ip>
```

```bash
dnf install -y dnsmasq
systemctl enable --now dnsmasq
firewall-cmd --permanent --add-service=dns
firewall-cmd --reload
```

### 2.4 Bastion CLI 준비

```bash
# OpenShift CLI 설치
cd /home/<bastion-user>/mas-install/ocp
tar -xvf openshift-client-linux.tar.gz
install -m 0755 oc kubectl /usr/local/bin/

# OpenShift Installer 설치
tar -xvf openshift-install-linux.tar.gz
install -m 0755 openshift-install /usr/local/bin/

# oc mirror 플러그인 설치
tar -xvf oc-mirror.tar.gz
install -m 0755 oc-mirror /usr/local/bin/oc-mirror

# MAS CLI 이미지 로드
docker load -i /home/<bastion-user>/mas-install/images/mas-cli-15.2.0.tar

oc version --client
openshift-install version
oc-mirror version
docker images | grep ibmmas
```

---

## 3. Mirror Registry 구성

MAS CLI의 registry setup 기능을 사용하거나, 사내 표준 Docker Registry / Quay / Harbor를 사용할 수 있습니다.

MAS CLI 방식:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  quay.io/ibmmas/cli:15.2.0 mas setup-registry
```

프롬프트 예시:

| 항목                  | 값                                        |
| --------------------- | ----------------------------------------- |
| Registry hostname     | `registry.<cluster-name>.<base-domain>` |
| Registry port         | `5000`                                  |
| Registry storage path | `/opt/registry`                         |

방화벽:

```bash
firewall-cmd --permanent --add-port=5000/tcp
firewall-cmd --reload
```

확인:

```bash
curl -k https://registry.<cluster-name>.<base-domain>:5000/v2/
```

---

## 4. 이미지 미러링

### 4.1 명령 검증 원칙

MAS CLI의 mirror 명령은 버전에 따라 옵션이 달라질 수 있습니다. 실제 미러링 전에 해당 버전의 help를 먼저 저장합니다.

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  quay.io/ibmmas/cli:15.2.0 mas mirror-images --help

docker run -ti --rm \
  -v ~:/mnt/home \
  quay.io/ibmmas/cli:15.2.0 mas mirror-redhat-images --help
```

> ⚠️ 미검증
>
> 아래 미러링 명령은 `quay.io/ibmmas/cli:15.2.0`의 실제 `--help` 출력과 대조한 뒤 실행해야 합니다. `mirror-images`는 최신 MAS CLI 문서에서 deprecated로 표시될 수 있지만, 고정된 구버전 CLI에서는 여전히 사용될 수 있습니다.

### 4.2 Phase 1 — 인터넷 PC에서 파일시스템으로 저장

Red Hat 이미지:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v /mnt/images:/mnt/images \
  quay.io/ibmmas/cli:15.2.0 mas mirror-redhat-images \
  --mode to-filesystem \
  --working-dir /mnt/images/redhat \
  --ocp-release 4.16 \
  --redhat-pullsecret /mnt/home/<pull-secret-file>
```

MAS 이미지:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v /mnt/images:/mnt/images \
  quay.io/ibmmas/cli:15.2.0 mas mirror-images \
  --mode to-filesystem \
  --working-dir /mnt/images/mas \
  --ibm-entitlement-key <ibm-entitlement-key> \
  --catalog-version v9-250828-amd64 \
  --mirror-mas-channel 9.2 \
  --mirror-manage
```

> 사용하는 MAS 애플리케이션에 따라 `--mirror-manage` 외 플래그가 필요할 수 있습니다. SLS, MongoDB, DB2 등 의존성 이미지 포함 여부도 `--help`와 설치 계획으로 확인합니다.

### 4.3 이미지 파일 Bastion으로 이동

`/mnt/images` 전체를 이동식 디스크나 파일 전송으로 Bastion의 `/mnt/images`로 복사합니다.

```bash
rsync -avh /mnt/images/ <bastion-user>@<bastion-ip>:/mnt/images/
```

### 4.4 Phase 2 — Bastion에서 Mirror Registry로 Push

Red Hat 이미지:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v /mnt/images:/mnt/images \
  quay.io/ibmmas/cli:15.2.0 mas mirror-redhat-images \
  --mode from-filesystem \
  --working-dir /mnt/images/redhat \
  --registry-host registry.<cluster-name>.<base-domain> \
  --registry-port 5000 \
  --registry-username <registry-user> \
  --registry-password <registry-password>
```

MAS 이미지:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v /mnt/images:/mnt/images \
  quay.io/ibmmas/cli:15.2.0 mas mirror-images \
  --mode from-filesystem \
  --working-dir /mnt/images/mas \
  --registry-host registry.<cluster-name>.<base-domain> \
  --registry-port 5000 \
  --registry-username <registry-user> \
  --registry-password <registry-password>
```

---

## 5. OCP SNO 설치

Agent-based installer를 사용하는 예시입니다.

### 5.1 `install-config.yaml`

```yaml
apiVersion: v1
baseDomain: <base-domain>
metadata:
  name: <cluster-name>
compute:
  - name: worker
    replicas: 0
controlPlane:
  name: master
  replicas: 1
networking:
  clusterNetwork:
    - cidr: 10.128.0.0/14
      hostPrefix: 23
  machineNetwork:
    - cidr: <machine-network-cidr>
  networkType: OVNKubernetes
  serviceNetwork:
    - 172.30.0.0/16
platform:
  none: {}
pullSecret: '<pull-secret-json>'
sshKey: '<ssh-public-key>'
imageDigestSources:
  - mirrors:
      - registry.<cluster-name>.<base-domain>:5000/openshift/release-images
    source: quay.io/openshift-release-dev/ocp-release
  - mirrors:
      - registry.<cluster-name>.<base-domain>:5000/openshift/release
    source: quay.io/openshift-release-dev/ocp-v4.0-art-dev
additionalTrustBundle: |
  <mirror-registry-ca>
```

> ⚠️ 미검증
>
> `imageDigestSources`의 mirror 경로는 `mas mirror-redhat-images` 또는 `oc mirror` 결과에 맞춰 조정해야 합니다. 위 경로는 예시이며, 실제 registry repository 구조와 다르면 설치 ISO가 release image를 Pull하지 못합니다.
>
> OpenShift 4.16 설치 방식에서 mirror 설정을 `install-config.yaml`에 넣을지, 설치 후 `ImageDigestMirrorSet`으로 넣을지는 현장 mirror 방식에 맞춰 선택합니다.

### 5.2 `agent-config.yaml`

```yaml
apiVersion: v1alpha1
kind: AgentConfig
metadata:
  name: <cluster-name>
rendezvousIP: <sno-ip>
hosts:
  - hostname: <sno-hostname>
    role: master
    interfaces:
      - name: <nic-name>
        macAddress: <nic-mac-address>
    networkConfig:
      interfaces:
        - name: <nic-name>
          type: ethernet
          state: up
          ipv4:
            enabled: true
            address:
              - ip: <sno-ip>
                prefix-length: <prefix-length>
            dhcp: false
      dns-resolver:
        config:
          server:
            - <bastion-ip>
      routes:
        config:
          - destination: 0.0.0.0/0
            next-hop-address: <gateway-ip>
            next-hop-interface: <nic-name>
```

### 5.3 ISO 생성 및 설치

```bash
mkdir -p ~/ocp-sno
cd ~/ocp-sno

openshift-install agent create image --dir .
```

생성된 ISO를 SNO 노드에 마운트하고 부팅합니다.

```bash
openshift-install agent wait-for bootstrap-complete --dir . --log-level=info
openshift-install agent wait-for install-complete --dir . --log-level=info
```

설치 후:

```bash
export KUBECONFIG=~/ocp-sno/auth/kubeconfig
oc get nodes
oc get co
```

### 5.4 스토리지 및 Registry

LVM Operator, RWX StorageClass, OpenShift Image Registry 구성은 [온라인 설치 문서 4장](INSTALL_ONLINE.md#4-스토리지-및-registry-구성)을 따릅니다.

---

## 6. Airgap 구성

미러링 완료 후 OCP 클러스터가 Mirror Registry를 사용하도록 설정합니다.

```bash
docker run -ti --rm \
  -v ~/.kube:/root/.kube \
  -v /home/<bastion-user>/mas-install/certs:/mnt/certs:ro \
  quay.io/ibmmas/cli:15.2.0 mas configure-airgap \
  -H registry.<cluster-name>.<base-domain> \
  -P 5000 \
  -u <registry-user> \
  -p <registry-password> \
  --ca-file /mnt/certs/<registry-ca-file> \
  --no-confirm
```

Red Hat catalog mirror 설정:

> ⚠️ 미검증
>
> `--setup-redhat-catalogs` 플래그는 MAS CLI 버전별로 지원 여부와 동작이 다를 수 있습니다. 실행 전 `quay.io/ibmmas/cli:15.2.0 mas configure-airgap --help`에서 지원 여부를 확인합니다.

```bash
docker run -ti --rm \
  -v ~/.kube:/root/.kube \
  quay.io/ibmmas/cli:15.2.0 mas configure-airgap \
  --setup-redhat-catalogs \
  -H registry.<cluster-name>.<base-domain> \
  -P 5000 \
  -u <registry-user> \
  -p <registry-password> \
  --no-confirm
```

확인:

```bash
oc get imagedigestmirrorset
oc get catalogsource -n openshift-marketplace
```

> `mas-and-dependencies`와 유사한 `ImageDigestMirrorSet`이 생성되는지 확인합니다. 이름은 MAS CLI 버전에 따라 달라질 수 있습니다.

---

## 7. MAS 9.2 설치

오프라인 환경에서도 기본 원칙은 온라인 설치와 동일합니다.

- `quay.io/ibmmas/cli:15.2.0` 사용
- `ibm-operator-catalog` catalog 사용
- RWO/RWX StorageClass 구분
- DB2/MongoDB 수동 생성 대신 MAS CLI 의존성 설치 흐름 우선

MAS CLI 실행:

```bash
docker run -ti --rm \
  -v ~:/mnt/home \
  -v ~/.kube:/root/.kube \
  quay.io/ibmmas/cli:15.2.0 mas install
```

입력값:

| 항목                 | 값                               |
| -------------------- | -------------------------------- |
| IBM Entitlement Key  | `<ibm-entitlement-key>`        |
| MAS License File     | `/mnt/home/<entitlement-file>` |
| Catalog Source       | `ibm-operator-catalog`         |
| Subscription Channel | `9.2`                          |
| MAS Instance ID      | `<mas-instance-id>`            |
| Workspace ID         | `<workspace-id>`               |
| Storage Class (RWO)  | `<rwo-storage-class>`          |
| Storage Class (RWX)  | `<rwx-storage-class>`          |

> `Catalog Source` 값은 실제 클러스터에 생성된 CatalogSource 이름과 일치해야 합니다. 설치 전 아래 명령으로 이름을 확인합니다.
>
> ```bash
> oc get catalogsource -n openshift-marketplace
> ```

> ⚠️ 미검증
>
> 폐쇄망에서 DB2/MongoDB를 별도 수동 설치하는 절차는 현재 문서에서 기본 설치 경로로 사용하지 않습니다. 외부 DB를 써야 한다면 `JdbcCfg`, `MongoCfg`, 인증 Secret, 네트워크 정책, 인증서 신뢰 구성을 별도 문서로 검증해야 합니다.

---

## 8. 설치 후 확인

```bash
oc get nodes
oc get co
oc get suite -A
oc get manageapp -A
oc get subscriptions -A
oc get pods -A | grep -v Running | grep -v Completed | grep -v Succeeded
```

Route 확인:

```bash
oc get route -A | grep -E 'mas|manage|admin'
```

IT 모듈 활성화와 BIRT 확인은 [온라인 설치 문서 6장](INSTALL_ONLINE.md#6-it-모듈-활성화), [7장](INSTALL_ONLINE.md#7-birt-구성)을 따릅니다.
