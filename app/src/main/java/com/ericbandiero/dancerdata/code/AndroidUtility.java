package com.ericbandiero.dancerdata.code;

import java.util.List;

/**
 * Class of helper methods
 * @author Eric Bandiero
 *
 */


public class AndroidUtility {

	   public static String getStringFromList(List<String>list){
		   
		    StringBuilder sb=new StringBuilder();
		   
		    for (String string : list) {
		        sb.append(string+System.getProperty("line.separator"));
		    }
		    sb.setLength(sb.length() - 1);
		    return sb.toString();
		}
}
