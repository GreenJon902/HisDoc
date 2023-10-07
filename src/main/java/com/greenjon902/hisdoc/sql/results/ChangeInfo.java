package com.greenjon902.hisdoc.sql.results;

public record ChangeInfo(String date, String author, String description) {
	public static ChangeInfo[] fromArrays(String[] changeDates, String[] changeAuthorInfos, String[] changeDescs) {
		if (changeDates.length != changeAuthorInfos.length && changeAuthorInfos.length != changeDescs.length) {
			throw new RuntimeException("Arrays should be of the same length");
		}
		ChangeInfo[] changeInfos = new ChangeInfo[changeDates.length];
		for (int i=0; i<changeInfos.length; i++) {
			changeInfos[i] = new ChangeInfo(changeDates[i], changeAuthorInfos[i], changeDescs[i]);
		}
		return changeInfos;
	}
}
