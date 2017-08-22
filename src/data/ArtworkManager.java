package data;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ArtworkManager {
	private static final String USER_DATA_DIR = ".mythfrontend";
	private static final String PROPERTIES_FILE = "index";
	private static final File DATA_PATH = new File(System.getProperty("user.home"), USER_DATA_DIR);

	private static Properties _artworkRegistry = new Properties();
	private static Map<String /* Filename */, ImageIcon /* Image */> _loadedArtwork = new HashMap<>();

	public static synchronized ImageIcon getArtwork(String uri) throws IOException {
		// 1. In Memory
		if (_loadedArtwork.containsKey(uri)) {
            return _loadedArtwork.get(uri);
        }
		
		// 2. On Disk
		if (_artworkRegistry.containsKey(uri)) {
			String filename = _artworkRegistry.getProperty(uri);
			ImageIcon image = new ImageIcon(new File(DATA_PATH, filename).getAbsolutePath());
			
			_loadedArtwork.put(uri, image); return image;
		}
		
		// 3. On Network
		System.out.println("Network Request: " + uri);
		StringBuffer fileNameBuffer = new StringBuffer();
		byte[] image = Source.image_get(uri, fileNameBuffer /* Out Param */);
		if (image == null)
			return null;
		
		String filename = fileNameBuffer.toString();
		ImageIcon iconImage = new ImageIcon(image, filename);
		if (!_artworkRegistry.containsValue(filename)) {
			_loadedArtwork.put(uri, iconImage);
			_artworkRegistry.put(uri, filename);
			
			// Store Image to File
			FileOutputStream outputStream = new FileOutputStream(new File(DATA_PATH, filename));
			outputStream.write(image);
			outputStream.close();
			
			// Store New Data to Properties File
			File propertiesFile = new File(DATA_PATH, PROPERTIES_FILE);
			try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
				_artworkRegistry.store(out, "Mapping of URIs to Filenames.");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return iconImage;
		} else {
			// Don't Store the Association, but return the Matched Image File
			if (_loadedArtwork.containsKey(filename))
				return _loadedArtwork.get(filename);
			else {
				ImageIcon diskImage = new ImageIcon(new File(DATA_PATH, filename).getAbsolutePath());
				_loadedArtwork.put(uri, diskImage); return diskImage;
			}
		}
	}
	
	public static void initializeArtworkManager() {
		// Create the Folder if Nonexistent
		if (!DATA_PATH.exists()) {
			boolean success = DATA_PATH.mkdirs();
			if (!success) return;
		}

		// Read from the Properties File if it Exists
		File propertiesFile = new File(DATA_PATH, PROPERTIES_FILE);
		if (propertiesFile.exists()) {
			try (FileInputStream in = new FileInputStream(propertiesFile)) {
				_artworkRegistry.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Look for Orphaned Picture Files
		Collection<Object> values = _artworkRegistry.values();
		String[] pictureFiles = DATA_PATH.list((File dir, String name) -> !name.equals(PROPERTIES_FILE));
		
		// Index & Disk File Maps
		Set<String> diskSet = new HashSet<>(Arrays.asList(pictureFiles == null ? new String[0] : pictureFiles));
		Set<String> diskSet2 = new HashSet<>(diskSet);
		Set<String> indexSet = new HashSet<>();
		for (Object obj : values) indexSet.add((String) obj);
		
		// Missing from Index Files
		diskSet.removeAll(indexSet);
		for (String s : diskSet) {
		    boolean success = (new File(DATA_PATH, s)).delete();
		    if (!success) System.err.println("Failed to delete " + s);
		}
		
		// Missing from Index Files
		indexSet.removeAll(diskSet2);
		for (String s : indexSet) values.remove(s);
		
		// Write Out Properties File
		try (FileOutputStream out = new FileOutputStream(propertiesFile)) {
			_artworkRegistry.store(out, "Mapping of URIs to Filenames.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean artworkDownloaded(String uri) {
		if (_loadedArtwork != null && _loadedArtwork.containsKey(uri))
			return true;
		else if (_artworkRegistry != null && _artworkRegistry.containsKey(uri))
			return true;
		return false;
	}
}
