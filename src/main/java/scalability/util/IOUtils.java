package scalability.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class IOUtils {

	public static String readResourceAsString(final String resource) throws IOException {
		return readResourceAsList(resource).stream()
				.collect(Collectors.joining("\n"));
	}

	public static List<String> readResourceAsList(final String resource) throws IOException {
		final List<String> lines = new ArrayList<>();
		try (
				final InputStream resourceStream = IOUtils.class.getClassLoader().getResourceAsStream(resource);
				final InputStreamReader inputStreamReader = new InputStreamReader(resourceStream);
				final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		) {
	        String line;
	        while ((line = bufferedReader.readLine()) != null) {
	        	lines.add(line);
	        }
		}
        return lines;
	}

    public static List<String> getFilesInResourceFolder(final String resourceFolder) throws IOException {
        final ClassLoader classLoader = IOUtils.class.getClassLoader();
        final Enumeration<java.net.URL> resources = classLoader.getResources(resourceFolder);
        final List<String> files = new ArrayList<>();
        while (resources.hasMoreElements()) {
            java.net.URL resourceURL = resources.nextElement();
            if (resourceURL.getProtocol().equals("jar")) {
            	getResourceFilesInJar(resourceURL, resourceFolder, files);
            } else {
                getResourceFilesInDirectory(resourceURL.getPath(), files);
            }
        }
        return files;
    }

	private static void getResourceFilesInJar(final URL jarUrl, final String resourceFolder, final List<String> file) throws IOException {
        String jarPath = jarUrl.getPath();
        jarPath = jarPath.substring(5, jarPath.indexOf("!"));
        jarPath = URLDecoder.decode(jarPath, "UTF-8");
        try (final JarFile jarFile = new JarFile(jarPath)) {
	        final Enumeration<JarEntry> entries = jarFile.entries();
	        while (entries.hasMoreElements()) {
	            JarEntry entry = entries.nextElement();
	            if (isValidResourceFile(entry.getName(), resourceFolder)) {
	                file.add(entry.getName());
	            }
	        }
        }
    }

	private static boolean isValidResourceFile(final String resource, final String resourceFolder) {
		return resource.startsWith(resourceFolder + "/")
				&& !resource.equals(resourceFolder + "/")
				&& !resource.endsWith("/");
	}

    private static void getResourceFilesInDirectory(final String path, final List<String> files) throws IOException {
    	getAllFiles(path).stream()
				.map(File::getPath)
				.map(IOUtils::getTargetPath)
				.forEach(files::add);
	}
    
    private static String getTargetPath(final String path) {
    	if (path.contains("target\\test-classes")) {
    		return path.split("target\\\\test-classes\\\\")[1];
    	} else {
    		return path.split("target\\\\classes\\\\")[1];
    	}
    }
	
    private static List<File> getAllFiles(final String dir) {
		final List<File> list = new ArrayList<>();
		getAllFiles(dir, list);
		return list;
	}

	private static void getAllFiles(final String dir, final List<File> files) {
		final File directory = new File(dir);
		final File[] fileList = directory.listFiles();
		if (fileList != null) {
			for (File file : fileList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					getAllFiles(file.getAbsolutePath(), files);
				}
			}
		}
	}

}
