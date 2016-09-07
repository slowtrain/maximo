package com.cafelivro.oslc;

import java.util.Iterator;


import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class MaximoTest {
    public static void main(String[] args) {
        try {
           // MaximoTest toMaximo=new MaximoTest("http","localhost","7001","maxadmin","maxadmin");
            //HTTPToMaximo toMaximo=new HTTPToMaximo("http://ayme:7001/maximo/oslc/os");


            //addAsset(toMaximo);
            //updateAssetTest(toMaximo);
            //queryTest(toMaximo);
            //querySavedQueryTest(toMaximo);
            //getSavedQueryTest(toMaximo);
            //addPerson(toMaximo);
            //updatePersonTest(toMaximo);
            //deleteTest(toMaximo);
            //attachementTest(toMaximo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*

    public static void addAsset(MaximoTest toMaximo){
        //Create JSONObject here
        JSONObject asset = new JSONObject();
        asset.put("spi:assetnum", "TEST001");
        asset.put("spi:description", "8151007test");
        asset.put("spi:siteid", "BEDFORD");
        toMaximo.add("oslcperson", asset);
    }

    public static void addPerson(MaximoTest toMaximo){
        //Create JSONObject here
        JSONObject person = new JSONObject();
        person.put("spi:personid", "TEST008");
        person.put("spi:displayname", "TEST001");

        toMaximo.add("oslcperson", person);
    }


    public static void updatePersonTest(MaximoTest toMaximo){


        String oslcWhere   = "oslc.where=spi:personid=\"TEST001\"";
        String oslcSelect  = "oslc.select=spi:personid,spi:displayname,spi:status";


        JSONObject result=toMaximo.query("oslcperson",oslcWhere,oslcSelect);
        JSONArray personSet =(JSONArray)result.get("rdfs:member");
        System.out.println("count : "+personSet.size());
        JSONObject person=(JSONObject)personSet.get(0);
        System.out.println(person.get("spi:personid"));


        JSONArray emailSet = new JSONArray();
        JSONObject email = new JSONObject();
        email.put("spi:emailaddress","test88888@cafelivro.com");
        email.put("spi:isprimary",true);
        email.put("spi:_action","Add");
        emailSet.add(email);

        person.put("spi:email",emailSet);



        JSONArray phoneSet = new JSONArray();
        JSONObject phone = new JSONObject();
        phone.put("spi:phonenum","017-8888-1005");
        phone.put("spi:isprimary",false);
        phone.put("spi:type","WORK");
        phone.put("spi:_action","Add");
        phone.put("spi:_action","Change");
        phone.put("spi:_action","Delete");
        phoneSet.add(phone);

        person.put("spi:phone",phoneSet);


        person.put("spi:displayname", "�씠�걶�씠7777777777");

        System.out.println(person);


        String resourceIdentifier=(String)person.get("rdf:about");
        System.out.println("resourceIdentifier");
        System.out.println(resourceIdentifier);
        toMaximo.update(resourceIdentifier, person);

    }


    public static void attachementTest(HTTPToMaximo toMaximo){
        String oslcWhere   = "oslc.where=spi:personid=\"TEST002\"";
        String oslcSelect  = "oslc.select=spi:doclinks";

        JSONObject result=toMaximo.query("oslcperson",oslcWhere,oslcSelect);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");
        System.out.println("count : "+assetSet.size());
        JSONObject asset=(JSONObject)assetSet.get(0);

        JSONObject doclinks=(JSONObject)asset.get("spi:doclinks");
        String resourceIdentifier =(String)doclinks.get("rdf:resource");
        System.out.println("identifier : "+resourceIdentifier);
        toMaximo.addAttachment(resourceIdentifier);
    }

    public static void updateAssetTest(HTTPToMaximo toMaximo){

        String oslcWhere   = "oslc.where=spi:personid=\"TEST001\"";
        String oslcSelect  = "oslc.select=spi:personid,spi:displayname,spi:status";

        JSONObject result=toMaximo.query("oslcasset",oslcWhere,oslcSelect);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");
        System.out.println("count : "+assetSet.size());
        JSONObject asset=(JSONObject)assetSet.get(0);

        asset.put("spi:changeby", "MAXADMIN");
        asset.put("spi:description", "1004 description ");



	    JSONObject assetMeter = new JSONObject();
	    assetMeter.put("spi:metername","TEMP-F");
	    assetMeter.put("spi:linearassetmeterid", 0);
	    assetMeter.put("spi:newreading", "29");
	    assetMeter.put("spi:_action", "Add");


	    JSONArray assetMeterSet= new JSONArray();
	    assetMeterSet.add(assetMeter);


	    asset.put("spi:assetmeter", assetMeterSet);


        String resourceIdentifier=(String)asset.get("rdf:about");
        toMaximo.update(resourceIdentifier, asset);
    }


    public static void deleteTest(HTTPToMaximo toMaximo){

        String oslcWhere   = "oslc.where=spi:personid=\"TEST002\"";
        String oslcSelect  = "oslc.select=spi:personid,spi:displayname,spi:status";

        JSONObject result=toMaximo.query("oslcperson",oslcWhere,oslcSelect);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");
        System.out.println("count : "+assetSet.size());
        JSONObject asset=(JSONObject)assetSet.get(0);

        String resourceIdentifier=(String)asset.get("rdf:about");
        toMaximo.delete(resourceIdentifier);
    }



    public static void queryTest(HTTPToMaximo toMaximo){

        String oslcWhere   = "oslc.where=spi:siteid=\"BEDFORD\" and spi:assetnum in [\"1005\"]";
        String oslcSelect  = "oslc.select=spi:assetnum,spi:description,spi:status,spi:assettype";
        String oslcOrderBy = "oslc.orderBy=+spi:assetnum";

        JSONObject result=toMaximo.query("oslcasset",oslcWhere,oslcSelect,oslcOrderBy);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");

        for(int i=0;i<assetSet.size();i++){
            JSONObject asset=(JSONObject)assetSet.get(i);
            String resourceIdentifier=(String)asset.get("rdf:about");
            String assetnum=(String)asset.get("spi:assetnum");
            String description=(String)asset.get("spi:description");
            String assetType=(String)asset.get("spi:assettype");
            System.out.println(resourceIdentifier);
            System.out.println(assetnum +" // "+description+" // "+assetType);

            Iterator it= asset.keySet().iterator();
            while(it.hasNext()){
                Object key = it.next();
                System.out.println(key.getClass().getSimpleName());
                System.out.println(key);
            }

            //	    asset.put("spi:description", "1026 update");
            //	    asset.put("spi:changeby", "MAXADMIN");

            //	    toMaximo.update(resourceIdentifier, asset);

        }

        System.out.println("patch count : "+assetSet.size());

    }



    public static void getSavedQueryTest(HTTPToMaximo toMaximo){
        String oslcWhere   = "oslc.where=spi:app=\"ASSET\"";
        String oslcSelect  = "oslc.select=spi:app,spi:clausename";


        JSONObject result=toMaximo.query("oslcquery",oslcWhere,oslcSelect);
        JSONArray querySet =(JSONArray)result.get("rdfs:member");

        for(int i=0;i<querySet.size();i++){
            JSONObject query=(JSONObject)querySet.get(i);
            System.out.println(query.get("spi:clausename")+" / "+query.get("spi:app"));


		    Iterator it= query.keySet().iterator();
		    while(it.hasNext()){
		    	Object key = it.next();
		    	System.out.println(key.getClass().getSimpleName());
		    	System.out.println(key);
		    	System.out.println(query.get(key));
		    }

        }


        System.out.println("patch count : "+querySet.size());
    }



    public static void querySavedQueryTest(HTTPToMaximo toMaximo){

        String savedQueryName   = "ITSTOCK";
        String savedQuerySelect  = "oslc.select=spi:assetnum,spi:description,spi:status,spi:assettype";
        String savedQueryWhere="spi:assetnum=\"2009\"";


        JSONObject result=toMaximo.querySavedQuery("oslcasset",savedQueryName,savedQuerySelect,savedQueryWhere);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");

        for(int i=0;i<assetSet.size();i++){
            JSONObject asset=(JSONObject)assetSet.get(i);
            String resourceIdentifier=(String)asset.get("rdf:about");
            String assetnum=(String)asset.get("spi:assetnum");
            String description=(String)asset.get("spi:description");
            String assetType=(String)asset.get("spi:assettype");
            //System.out.println(resourceIdentifier);
            System.out.println(assetnum +" // "+description+" // "+assetType);



//		    Iterator it= asset.keySet().iterator();
//		    while(it.hasNext()){
//		    	Object key = it.next();
//		    	System.out.println(key.getClass().getSimpleName());
//		    	System.out.println(key);
//		    }

//    	    asset.put("spi:description", "1026 update");
//		    asset.put("spi:changeby", "MAXADMIN");
//
//		    toMaximo.update(resourceIdentifier, asset);

        }


        System.out.println("patch count : "+assetSet.size());

    }

    */




}

