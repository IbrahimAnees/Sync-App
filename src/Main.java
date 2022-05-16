// Author: Ibrahim Anees (UPI: iane056)
// SE370 A2

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ParseException, InterruptedException {
		File dir1 = new File(args[0]);
		File dir2 = new File(args[1]);
		if (checkValidInput(dir1, dir2, args)) {


			
			// CREATING/UPDATING SYNC FILES:
			if (checkSyncFile(dir1)) {populateSyncFile(dir1);} // if sync file does NOT exist
			else { updateMap(dir1);} // if sync file exists

			if (checkSyncFile(dir2)) {populateSyncFile(dir2);} // if sync file does NOT exist
			else { updateMap(dir2);} // if sync file exists

			// MERGING DIRECTORIES:
			mergeDirectories(dir1, dir2);
			
			// DEALING WITH SUBDIRECTORIES:
			dealWithSubdirs(dir1, dir2);

			Thread.sleep(1000);
		}
	}

	// Deals with subdirectories
	public static void dealWithSubdirs(File dir1, File dir2) throws NoSuchAlgorithmException, IOException, ParseException {
		ArrayList<File> subDirs1 = listSubdirs(dir1);
		ArrayList<File> subDirs2 = listSubdirs(dir2);
		
		for (File dir : subDirs1) {

			String path1 = dir.getPath();
			String temp = path1.substring(path1.indexOf(File.separator) + 1);
			temp.trim();
			String relativePath1 = temp;
			String newPath = dir2.getName().concat(File.separator).concat(relativePath1);
			//System.out.println(newPath);
			File dirFile = new File(newPath);
			if (!dirFile.exists()) {
				FileUtils.copyDirectory(dir, dirFile);
			}

		}
		
		for (File dir : subDirs2) {

			String path1 = dir.getPath();
			String temp = path1.substring(path1.indexOf(File.separator) + 1);
			temp.trim();
			String relativePath2 = temp;
			String newPath = dir1.getName().concat(File.separator).concat(relativePath2);
			//System.out.println(newPath);
			File dirFile = new File(newPath);
			if (!dirFile.exists()) {
				FileUtils.copyDirectory(dir, dirFile);
			}
		}
		ArrayList<File> subDirs11 = listSubdirs(dir1);
		ArrayList<File> subDirs22 = listSubdirs(dir2);
		
		for (File dir : subDirs11) {
			if (checkSyncFile(dir)) {populateSyncFile(dir);}
			else {updateMap(dir);}
			

		}
		
		for (File dir : subDirs22) {
			if (checkSyncFile(dir)) {populateSyncFile(dir);}
			else {updateMap(dir);}
			
			String path1 = dir.getPath();
			String temp = path1.substring(path1.indexOf(File.separator) + 1);
			temp.trim();
			String relativePath1 = temp;
			String newPath = dir1.getName().concat(File.separator).concat(relativePath1);
			String oldPath = dir2.getName().concat(File.separator).concat(relativePath1);
			//System.out.println(newPath);
			File dir11 = new File(newPath);
			File dir22 = new File(oldPath);
			mergeDirectories(dir11, dir22);
		}
	}
	
	// Helper method for dealWithSubdirs
	public static ArrayList<File> listSubdirs(File file) {
	    List<File> temp0 = Arrays.asList(file.listFiles(new FileFilter() {
	        public boolean accept(File f) {
	            return f.isDirectory();}}));
	    temp0 = new ArrayList<File>(temp0);

	    List<File> temp = new ArrayList<File>();
	    for(File f : temp0) {
	        temp.addAll(listSubdirs(f)); 
	    }
	    temp0.addAll(temp);
        ArrayList<File> result = new ArrayList<File>(temp0);
	    return result;
	}
	
	// Merges two directories together after sync files have been updated
	public static void mergeDirectories(File dir1, File dir2) throws IOException, ParseException, NoSuchAlgorithmException {

		Map<String, ArrayList<ArrayList<String>>> syncMap11 = retrieveMap(dir1);
		Map<String, ArrayList<ArrayList<String>>> syncMap22 = retrieveMap(dir2);

		ArrayList<File> filesInDirectory11 = new ArrayList<>(Arrays.asList(dir1.listFiles()));
		ArrayList<File> filesInDirectory22 = new ArrayList<>(Arrays.asList(dir2.listFiles()));
		
		Iterator<File> iter111 = filesInDirectory11.iterator();
		while (iter111.hasNext()) {
			File fx = iter111.next();
			if (fx.isDirectory()) {
				iter111.remove();
			}
		}
		
		Iterator<File> iter112 = filesInDirectory22.iterator();
		while (iter112.hasNext()) {
			File fx = iter112.next();
			if (fx.isDirectory()) {
				iter112.remove();
			}
		}
		

		Iterator<File> iter11 = filesInDirectory11.iterator();
		while (iter11.hasNext()) {
			String strr = iter11.next().getName();
			if ((strr.charAt(0) == '.')) {
				iter11.remove();
			}
		}


		Iterator<File> iter22 = filesInDirectory22.iterator();
		while (iter22.hasNext()) {
			String strr = iter22.next().getName();
			if ((strr.charAt(0) == '.')) {
				iter22.remove();
			}
		}

		ArrayList<String> filesStrInDirectory11 = new ArrayList<String>();
		ArrayList<String> filesStrInDirectory22 = new ArrayList<String>();

		for (File f : filesInDirectory11) filesStrInDirectory11.add(f.getName());
		for (File f : filesInDirectory22) filesStrInDirectory22.add(f.getName());





		Map<String, ArrayList<ArrayList<String>>> syncMap1 = retrieveMap(dir1);
		Map<String, ArrayList<ArrayList<String>>> syncMap2 = retrieveMap(dir2);

		ArrayList<File> filesInDirectory1 = new ArrayList<>(Arrays.asList(dir1.listFiles()));
		ArrayList<File> filesInDirectory2 = new ArrayList<>(Arrays.asList(dir2.listFiles()));

		Iterator<File> iter1111 = filesInDirectory1.iterator();
		while (iter1111.hasNext()) {
			File fx = iter1111.next();
			if (fx.isDirectory()) {
				iter1111.remove();
			}
		}
		
		Iterator<File> iter1122 = filesInDirectory2.iterator();
		while (iter1122.hasNext()) {
			File fx = iter1122.next();
			if (fx.isDirectory()) {
				iter1122.remove();
			}
		}
		
		
		Iterator<File> iter1 = filesInDirectory1.iterator();
		while (iter1.hasNext()) {
			String str = iter1.next().getName();
			if ((str.charAt(0) == '.')) {
				iter1.remove();
			}
		}


		Iterator<File> iter2 = filesInDirectory2.iterator();
		while (iter2.hasNext()) {
			String str = iter2.next().getName();
			if ((str.charAt(0) == '.')) {
				iter2.remove();
			}
		}

		ArrayList<String> filesStrInDirectory1 = new ArrayList<String>();
		ArrayList<String> filesStrInDirectory2 = new ArrayList<String>();

		for (File f : filesInDirectory1) filesStrInDirectory1.add(f.getName());
		for (File f : filesInDirectory2) filesStrInDirectory2.add(f.getName());


		for (int i = 0; i < filesStrInDirectory1.size(); i++) {
			for (int j = 0; j < filesStrInDirectory2.size(); j++) {
				if (filesStrInDirectory1.get(i).equals(filesStrInDirectory2.get(j))) {
					if (syncMap1.get(filesStrInDirectory1.get(i)).get(0).get(1).equals(syncMap2.get(filesStrInDirectory2.get(j)).get(0).get(1))) {
						dealWithSameDigests(filesInDirectory1.get(i), filesInDirectory2.get(j), dir1, dir2, syncMap1, syncMap2);
					}
					else {
						String topHashFromFile1 = syncMap1.get(filesStrInDirectory1.get(i)).get(0).get(1);
						ArrayList<String> allHashesFromFile2 = new ArrayList<String>();

						ArrayList<ArrayList<String>> file2values = syncMap2.get(filesStrInDirectory2.get(j));
						for (ArrayList<String> pair : file2values) {
							allHashesFromFile2.add(pair.get(1));
						}

						String topHashFromFile2 = syncMap2.get(filesStrInDirectory2.get(j)).get(0).get(1);
						ArrayList<String> allHashesFromFile1 = new ArrayList<String>();

						ArrayList<ArrayList<String>> file1values = syncMap1.get(filesStrInDirectory1.get(i));
						for (ArrayList<String> pair : file1values) {
							allHashesFromFile1.add(pair.get(1));
						}

						if (allHashesFromFile2.contains(topHashFromFile1)) {
							dealWithEarlierDigest(filesInDirectory1.get(i), filesInDirectory2.get(j), dir1, dir2, syncMap1, syncMap2, "2latest");
						}
						else if (allHashesFromFile1.contains(topHashFromFile2)) {
							dealWithEarlierDigest(filesInDirectory1.get(i), filesInDirectory2.get(j), dir1, dir2, syncMap1, syncMap2, "1latest");
						}
						else {
							dealWithUniqueDigests(filesInDirectory1.get(i), filesInDirectory2.get(j), dir1, dir2, syncMap1, syncMap2);
						}
					}
				}
			}
		}
		dealWithDeletions(filesInDirectory11, filesInDirectory22, filesStrInDirectory11, filesStrInDirectory22, dir1, dir2, syncMap11, syncMap22);
		dealWithUniqueFiles(filesInDirectory1, filesInDirectory2, filesStrInDirectory1, filesStrInDirectory2, dir1, dir2, syncMap1, syncMap2);
	}
	
	// Helper method for mergeDirectories
	public static void dealWithDeletions(ArrayList<File> filesInDirectory1, ArrayList<File> filesInDirectory2, ArrayList<String> filesStrInDirectory1, ArrayList<String> filesStrInDirectory2, File dir1, File dir2, Map<String, ArrayList<ArrayList<String>>> syncMap1, Map<String, ArrayList<ArrayList<String>>> syncMap2) throws IOException, ParseException {

		ArrayList<String> keyset1 = new ArrayList<>(syncMap1.keySet());
		ArrayList<String> keyset2 = new ArrayList<>(syncMap2.keySet());		

		if (keyset1 != null) {
			for (String key : keyset1) {
				String digest = syncMap1.get(key).get(0).get(1);

				if (new File(dir2.toString() + File.separator + key).isFile()) {
					if (digest.equals("deleted")) {

						String modifiedTime1str = syncMap1.get(key).get(0).get(0);
						String modifiedTime2str = syncMap2.get(key).get(0).get(0);

						SimpleDateFormat form1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
						Date d1 = form1.parse(modifiedTime1str);
						long time1 = d1.getTime();

						SimpleDateFormat form2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
						Date d2 = form2.parse(modifiedTime2str);
						long time2 = d2.getTime();

						if (time1 >= time2) {

							File toBeDeleted = new File(dir2.toString() + File.separator + key);
							File toBeDeleted2 = new File(dir1.toString() + File.separator + key);

							toBeDeleted.delete();
							toBeDeleted2.delete();
							ArrayList<String> deletedEntry = syncMap1.get(key).get(0);
							ArrayList<ArrayList<String>> temp = syncMap2.get(key);
							temp.add(0, deletedEntry);
							syncMap2.put(key, temp);

							Path path = Paths.get(dir2 + File.separator + ".sync");
							try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
								Gson gson = new GsonBuilder().setPrettyPrinting().create();
								JsonElement tree = gson.toJsonTree(syncMap2);
								gson.toJson(tree, writer);
							}
						}
					}
				}
			}
		}


		if (keyset2 != null) {
			for (String key : keyset2) {
				String digest = syncMap2.get(key).get(0).get(1);

				if (new File(dir1.toString() + File.separator + key).isFile()) {
					if (digest.equals("deleted")) {

						String modifiedTime1str = syncMap1.get(key).get(0).get(0);
						String modifiedTime2str = syncMap2.get(key).get(0).get(0);

						SimpleDateFormat form1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
						Date d1 = form1.parse(modifiedTime1str);
						long time1 = d1.getTime();

						SimpleDateFormat form2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
						Date d2 = form2.parse(modifiedTime2str);
						long time2 = d2.getTime();

						if (time2 >= time1) {



							File toBeDeleted = new File(dir1.toString() + File.separator + key);
							File toBeDeleted2 = new File(dir2.toString() + File.separator + key);


							toBeDeleted.delete();
							toBeDeleted2.delete();
							ArrayList<String> deletedEntry = syncMap2.get(key).get(0);
							ArrayList<ArrayList<String>> temp = syncMap1.get(key);
							temp.add(0, deletedEntry);
							syncMap1.put(key, temp);

							Path path = Paths.get(dir1 + File.separator + ".sync");
							try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
								Gson gson = new GsonBuilder().setPrettyPrinting().create();
								JsonElement tree = gson.toJsonTree(syncMap1);
								gson.toJson(tree, writer);
							}
						}
					}
				}
			}
		}
	}

	// Helper method for mergeDirectories
	public static void dealWithEarlierDigest(File file1, File file2, File dir1, File dir2, Map<String, ArrayList<ArrayList<String>>> syncMap1, Map<String, ArrayList<ArrayList<String>>> syncMap2, String type) throws ParseException, IOException, NoSuchAlgorithmException {
		if (type.equals("1latest")) {
			FileUtils.copyFileToDirectory(file1, dir2, true);
			ArrayList<String> temp = syncMap1.get(file1.getName()).get(0);
			ArrayList<ArrayList<String>> tempS = syncMap2.get(file1.getName());
			tempS.add(0, temp);
			syncMap2.put(file1.getName(), tempS);
			Path path = Paths.get(dir2 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap2);
				gson.toJson(tree, writer);
			}
		}
		else {
			FileUtils.copyFileToDirectory(file2, dir1, true);
			ArrayList<String> temp = syncMap2.get(file2.getName()).get(0);
			ArrayList<ArrayList<String>> tempS = syncMap1.get(file2.getName());
			tempS.add(0, temp);
			syncMap1.put(file2.getName(), tempS);
			Path path = Paths.get(dir1 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap1);
				gson.toJson(tree, writer);
			}
		}
	}

	// Helper method for mergeDirectories
	public static void dealWithUniqueDigests(File file1, File file2, File dir1, File dir2, Map<String, ArrayList<ArrayList<String>>> syncMap1, Map<String, ArrayList<ArrayList<String>>> syncMap2) throws ParseException, IOException, NoSuchAlgorithmException {
		String time1str = syncMap1.get(file1.getName()).get(0).get(0);
		String time2str = syncMap2.get(file1.getName()).get(0).get(0);
		SimpleDateFormat form1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date d1 = form1.parse(time1str);
		long time1 = d1.getTime();

		SimpleDateFormat form2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date d2 = form2.parse(time2str);
		long time2 = d2.getTime();

		if (time1 <= time2) {
			FileUtils.copyFileToDirectory(file2, dir1, true);
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(time2str);
			temp.add(hashFile(file2));
			ArrayList<ArrayList<String>> temp2 = syncMap1.get(file1.getName());
			temp2.add(0, temp);
			syncMap1.put(file1.getName(), temp2);
			Path path = Paths.get(dir1 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap1);
				gson.toJson(tree, writer);
			}
		}
		else {
			FileUtils.copyFileToDirectory(file1, dir2, true);
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(time1str);
			temp.add(hashFile(file1));
			ArrayList<ArrayList<String>> temp2 = syncMap2.get(file2.getName());
			temp2.add(0, temp);
			syncMap2.put(file2.getName(), temp2);
			Path path = Paths.get(dir2 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap2);
				gson.toJson(tree, writer);
			}
		}
	}
	
	// Helper method for mergeDirectories
	public static void dealWithUniqueFiles(ArrayList<File> F1, ArrayList<File> F2, ArrayList<String> filesStrInDirectory1, ArrayList<String> filesStrInDirectory2, File dir1, File dir2, Map<String, ArrayList<ArrayList<String>>> syncMap1, Map<String, ArrayList<ArrayList<String>>> syncMap2) throws IOException {

		ArrayList<File> filesInDirectory1 = new ArrayList<>(Arrays.asList(dir1.listFiles()));
		ArrayList<File> filesInDirectory2 = new ArrayList<>(Arrays.asList(dir2.listFiles()));

		Iterator<File> iter111 = filesInDirectory1.iterator();
		while (iter111.hasNext()) {
			File fx = iter111.next();
			if (fx.isDirectory()) {
				iter111.remove();
			}
		}
		
		Iterator<File> iter112 = filesInDirectory2.iterator();
		while (iter112.hasNext()) {
			File fx = iter112.next();
			if (fx.isDirectory()) {
				iter112.remove();
			}
		}
		
		

		Iterator<File> iter1 = filesInDirectory1.iterator();
		while (iter1.hasNext()) {
			String str = iter1.next().getName();
			if ((str.charAt(0) == '.')) {
				iter1.remove();
			}
		}


		Iterator<File> iter2 = filesInDirectory2.iterator();
		while (iter2.hasNext()) {
			String str = iter2.next().getName();
			if ((str.charAt(0) == '.')) {
				iter2.remove();
			}
		}

		ArrayList<File> uniqueFiles = new ArrayList<File>();
		if (filesInDirectory1 != null) {
			for (File f : filesInDirectory1) {
				if (!(filesStrInDirectory2.contains(f.getName()))) {
					uniqueFiles.add(f);
				}
			}
		}

		if (filesInDirectory2 != null) {
			for (File f : filesInDirectory2) {
				if (!(filesStrInDirectory1.contains(f.getName()))) {
					uniqueFiles.add(f);
				}
			}
		}

		Map<String, ArrayList<ArrayList<String>>> syncMap11 = retrieveMap(dir1);
		Map<String, ArrayList<ArrayList<String>>> syncMap22 = retrieveMap(dir2);

		for (File f : uniqueFiles) {


			boolean directory1 = filesInDirectory1.contains(f);
			if (directory1) {
				ArrayList<String> toBeInserted = syncMap11.get(f.getName()).get(0);
				ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
				if (syncMap22.containsKey(f.getName())) {
				temp = syncMap22.get(f.getName());
				}
				temp.add(0, toBeInserted);
				syncMap22.put(f.getName(), temp);
				Path path = Paths.get(dir2 + File.separator + ".sync");
				try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonElement tree = gson.toJsonTree(syncMap22);
					gson.toJson(tree, writer);
				}
				FileUtils.copyFileToDirectory(f, dir2, true);
			}
			else {
				ArrayList<String> toBeInserted = syncMap22.get(f.getName()).get(0);
				ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
				if (syncMap11.containsKey(f.getName())) {
				temp = syncMap11.get(f.getName());
				}
				temp.add(0, toBeInserted);
				syncMap11.put(f.getName(), temp);
				Path path = Paths.get(dir1 + File.separator + ".sync");
				try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					JsonElement tree = gson.toJsonTree(syncMap11);
					gson.toJson(tree, writer);
				}
				FileUtils.copyFileToDirectory(f, dir1, true);
			}
		}
	}

	// Helper method for mergeDirectories
	public static void dealWithSameDigests(File file1, File file2, File dir1, File dir2, Map<String, ArrayList<ArrayList<String>>> syncMap1, Map<String, ArrayList<ArrayList<String>>> syncMap2) throws IOException, ParseException {
		ArrayList<ArrayList<String>> temp1 = syncMap1.get(file1.getName());
		String timeToBeSet1 = temp1.get(0).get(0);
		SimpleDateFormat form1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date d1 = form1.parse(timeToBeSet1);
		long time1 = d1.getTime();

		ArrayList<ArrayList<String>> temp2 = syncMap2.get(file2.getName());
		String timeToBeSet2 = temp2.get(0).get(0);
		SimpleDateFormat form2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Date d2 = form2.parse(timeToBeSet2);
		long time2 = d2.getTime();

		if (time1 > time2) {
			String hash = temp1.get(0).get(1);
			ArrayList<String> temps = new ArrayList<String>();
			temps.add(timeToBeSet2);
			temps.add(hash);
			temp1.set(0, temps);
			syncMap1.put(file1.getName(), temp1);

			Path path = Paths.get(dir1 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap1);
				gson.toJson(tree, writer);
			}

			file1.setLastModified(time2);
			file2.setLastModified(time2);
		}
		if (time1 < time2) {
			String hash2 = temp2.get(0).get(1);
			ArrayList<String> temps2 = new ArrayList<String>();
			temps2.add(timeToBeSet1);
			temps2.add(hash2);
			temp2.set(0, temps2);
			syncMap2.put(file2.getName(), temp2);

			Path path = Paths.get(dir2 + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(syncMap2);
				gson.toJson(tree, writer);
			}

			file1.setLastModified(time1);
			file2.setLastModified(time1);
		}
		if (time1 == time2) {
			file1.setLastModified(time1);
			file2.setLastModified(time1);
		}
	}

	// Updates current sync file to reflect correct values
	public static void updateMap(File dir) throws IOException, NoSuchAlgorithmException, ParseException {		

		Map<String, ArrayList<ArrayList<String>>> filesMap = retrieveMap(dir);
		File[] temp = dir.listFiles();
		ArrayList<File> filesInDirectory = new ArrayList<>(Arrays.asList(temp));

		Iterator<File> iter11 = filesInDirectory.iterator();
		while (iter11.hasNext()) {
			File fx = iter11.next();
			if (fx.isDirectory()) {
				iter11.remove();
			}
		}
		
		dealWithDeleted(filesMap, filesInDirectory, dir);
		dealWithNew(filesMap, filesInDirectory, dir);
		dealWithUpdated(filesMap, filesInDirectory, dir);
	}

	// Helper method for updateMap which deals with changes to files
	public static void dealWithUpdated(Map<String, ArrayList<ArrayList<String>>> syncMap, ArrayList<File> filesInDirectory, File dir) throws IOException, NoSuchAlgorithmException, ParseException {
		for (File f : filesInDirectory) {
			if (!(f.getName().charAt(0) == '.')) {
				String latestHash = hashFile(f);
				String oldHash = syncMap.get(f.getName()).get(0).get(1);
				if (!latestHash.equals(oldHash)) {
					ArrayList<ArrayList<String>> temp = syncMap.get(f.getName());
					ArrayList<String> temp2 = new ArrayList<String>();
					Date d = new Date(f.lastModified());
					SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
					String modifiedTime = form.format(d);					temp2.add(modifiedTime);
					temp2.add(latestHash);
					temp.add(0, temp2);
					syncMap.put(f.getName(), temp);

					Path path = Paths.get(dir + File.separator + ".sync");
					try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
						Gson gson = new GsonBuilder().setPrettyPrinting().create();
						JsonElement tree = gson.toJsonTree(syncMap);
						gson.toJson(tree, writer);
					}
				}
				else {
					ArrayList<ArrayList<String>> temp = syncMap.get(f.getName());
					String timeToBeSet = temp.get(0).get(0);
					SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
					Date d = form.parse(timeToBeSet);
					long correctTime = d.getTime();
					long wrongTime = f.lastModified();
					if (correctTime != wrongTime) {
						//System.out.println("Successfully updated time!");
						f.setLastModified(correctTime);
					}
				}
			}
		}
	}

	// Helper method for updateMap which deals with new files
	public static void dealWithNew(Map<String, ArrayList<ArrayList<String>>> filesMap, ArrayList<File> filesInDirectory, File dir) throws IOException, NoSuchAlgorithmException {
		ArrayList<String> temp = new ArrayList<>(filesMap.keySet());
		ArrayList<String> directoryFiles = new ArrayList<String>();
		for (File f : filesInDirectory) {
			directoryFiles.add(f.getName());
		}
		directoryFiles.removeAll(temp);
		directoryFiles.remove(".sync");


		Iterator<String> iter = directoryFiles.iterator();
		while (iter.hasNext()) {
			String str = iter.next();

			if ((str.charAt(0) == '.')) {
				iter.remove();
			}
		}


		ArrayList<String> newFiles = directoryFiles;
		for (String newFile : newFiles) {
			ArrayList<ArrayList<String>> toBeInserted = new ArrayList<ArrayList<String>>();
			ArrayList<String> toBeInserted2 = new ArrayList<String>();
			File f = new File(dir + File.separator + newFile);
			Date d = new Date(f.lastModified());
			SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
			String modifiedTime = form.format(d);

			toBeInserted2.add(modifiedTime);
			toBeInserted2.add(hashFile(f));
			toBeInserted.add(toBeInserted2);
			filesMap.put(newFile, toBeInserted);

			Path path = Paths.get(dir + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(filesMap);
				gson.toJson(tree, writer);
			}
		}
	}


	// Helper method for updateMap which deals with deleted files
	@SuppressWarnings("unlikely-arg-type")
	public static void dealWithDeleted(Map<String, ArrayList<ArrayList<String>>> filesMap, ArrayList<File> filesInDirectory, File dir) throws IOException {
		ArrayList<String> deleted = new ArrayList<>(filesMap.keySet());
		deleted.removeAll(filesInDirectory);
		for (String deletedFile : deleted) {
			ArrayList<ArrayList<String>> temp = filesMap.get(deletedFile);

			boolean isReadded = false;
			for (File f : filesInDirectory) {
				if (f.getName().equals(deletedFile)) {
					isReadded = true;
					break;
				}
			}

			if (!(temp.get(0).get(1).equals("deleted")) && !(isReadded)) {
				Date d = new Date(System.currentTimeMillis());
				SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
				String modifiedTime = form.format(d);
				String[] temp0 = {modifiedTime, "deleted"};
				ArrayList<String> deletedEntry = new ArrayList<>(Arrays.asList(temp0));
				temp.add(0, deletedEntry);
				filesMap.put(deletedFile, temp);
			}
		}
		Path path = Paths.get(dir + File.separator + ".sync");
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonElement tree = gson.toJsonTree(filesMap);
			gson.toJson(tree, writer);
		}

	}

	// Returns Map of sync file of the given directory
	public static Map<String, ArrayList<ArrayList<String>>> retrieveMap(File dir) throws IOException {
		String hashString = new String(Files.readAllBytes(Paths.get(dir + File.separator + ".sync")));
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, ArrayList<ArrayList<String>>>>(){}.getType();
		Map<String, ArrayList<ArrayList<String>>> myMap = gson.fromJson(hashString, type);
		return myMap;
	}


	// Populates sync file for the first time
	public static void populateSyncFile(File dir) throws NoSuchAlgorithmException, IOException {
		//Map<String, String[][]> filesMap = new HashMap<>();
		Map<String, ArrayList<ArrayList<String>>> fMap = new HashMap<String, ArrayList<ArrayList<String>>>();

		File[] filesInDirectories = dir.listFiles();
		ArrayList<File> filesInDirectory = new ArrayList<>(Arrays.asList(filesInDirectories));
		
		Iterator<File> iter11 = filesInDirectory.iterator();
		while (iter11.hasNext()) {
			File fx = iter11.next();
			if (fx.isDirectory()) {
				iter11.remove();
			}
		}
		
		if (filesInDirectory != null) {
			for (File f : filesInDirectory) {
				if (!(f.getName().charAt(0) == '.')) {
					String hashValue = hashFile(f);
					String fileName = f.getName();
					Date d = new Date(f.lastModified());
					SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
					String modifiedTime = form.format(d);
					ArrayList<String> dateAndHash = new ArrayList<String>();
					dateAndHash.add(modifiedTime);
					dateAndHash.add(hashValue);
					ArrayList<ArrayList<String>> filePairs = new ArrayList<ArrayList<String>>();
					filePairs.add(dateAndHash);
					fMap.put(fileName, filePairs);
				}
			}
			Path path = Paths.get(dir + File.separator + ".sync");
			try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonElement tree = gson.toJsonTree(fMap);
				gson.toJson(tree, writer);
			}
		}
	}

	// Returns hash value of a sync file
	private static String hashFile(File file) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		FileInputStream temp = new FileInputStream(file);
		byte[] initial = new byte[1024];
		int count = 0; 
		while (!((count = temp.read(initial)) == -1)) 
			digest.update(initial, 0, count);

		byte[] hash = digest.digest();
		StringBuilder hashedFile = new StringBuilder();
		for(int i=0; i< hash.length ;i++)
			hashedFile.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
		temp.close();
		String hashString = hashedFile.toString();
		return hashString;
	}


	// Creates a sync file if one does not exist
	public static boolean checkSyncFile(File dir) throws IOException, NoSuchAlgorithmException {
		boolean check = new File(dir, ".sync").exists();
		if (!check) {
			File syncFile = new File(dir + File.separator + ".sync");
			syncFile.createNewFile();
			return true;
		}
		return false;
	}


	// Checks if inputs are valid, and creates a second directory if only one valid
	public static boolean checkValidInput(File dir1, File dir2, String[] args) throws IOException {
		int numberOfDirectories = args.length;
		if (numberOfDirectories != 2) {
			System.out.println("Error: Please enter two valid directories!");
			return false;
		}
		else {

			if (dir1.isFile() || dir2.isFile()) {
				System.out.println("Error: Do not enter files as input!");
				return false;
			}
			else if ((!dir1.isDirectory()) && (!dir2.isDirectory())) {
				System.out.println("Error: Please enter two valid directories!");
				return false;
			}
			else if (dir1.isDirectory() && (!dir2.isDirectory())) {
				//System.out.println("1 true: " + args[0] + " " + args[1]);
				File f = new File(args[1]);
				FileUtils.copyDirectory(dir1, f);
				return true;
			}
			else if ((!dir1.isDirectory()) && (dir2.isDirectory())) {
				//System.out.println("2 true: " + args[0] + " " + args[1]);
				File f = new File(args[0]);
				FileUtils.copyDirectory(dir2, f);
				return true;
			}
			else {
				//System.out.println("both true: " + args[0] + " " + args[1]);
				return true;
			}
		}
	}

}