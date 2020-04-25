package `in`.bajajtech.apps.utils

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

object JSONHelper {
    fun parseResponse(source: String, valueTag: String, failTag: String): Pair<Boolean, Any?>{
//        println(source)
        val parser = JSONParser()
        try {
            val rootObject = parser.parse(source) as JSONObject
            val status = rootObject["status"]
//            println(status)
            return if(status == "success"){
                if(rootObject[valueTag]!=null){
//                    println(rootObject[valueTag])
                    Pair(true,rootObject[valueTag])
                }else{
                    Pair(true,null)
                }
            }else{
                if(rootObject[failTag]!=null){
//                    println(rootObject[valueTag])
                    Pair(false,rootObject[failTag])
                }else{
                    Pair(false,null)
                }
            }
        }catch(ex: Exception){
            return Pair(false,null)
        }
    }
}