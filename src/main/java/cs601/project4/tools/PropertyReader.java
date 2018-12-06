package cs601.project4.tools;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 * basic reader for configuration file
 * @author yangzun
 *
 */
public class PropertyReader {
	
	private static Logger logger = Logger.getLogger(PropertyReader.class);
	static {
		PropertyConfigurator.configure("./config/log4j.properties");
	}
	
	protected String root;
	private String fileName = "*.properties";
	protected Properties props = new Properties();
	
	
	public PropertyReader(String root) {
		super();
		this.root = root;
		init();
	}
	
	public PropertyReader(String root, String fileName) {
		super();
		this.root = root;
		this.fileName = fileName;
		init();
	}
	
	public void init(){
		Path path = Paths.get(root);//"./conf"
		try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path, this.fileName)) {
			Path filePath;
			for ( Path p : directoryStream ) {
				String fileName = p.getFileName().toString();
				filePath = path.resolve( fileName );
				if (filePath.toFile().isFile()) {
					props.load(Files.newInputStream(filePath));
				}
			}
		} catch ( IOException e ) {
			logger.info("configuration directory read error");
		}
	}

	
	/**
	 * read a string value associated with a key, if failed, without a defaultValue
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String readStringValue( String key) {
		String value = props.getProperty( key, "" );
		if ( value == null || value.isEmpty() ) {
			value = "";
		}
		return value;
	}
	
	/**
	 * read a string value associated with a key, if failed, give a defaultValue
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String readStringValueWithDefault( String key, String defaultValue ) {
		String value = props.getProperty( key, defaultValue );
		if ( value == null || value.isEmpty() ) {
			value = defaultValue;
		}
		return value;
	}
	
	/**
	 * read a value associated with a key and try to turn it into an integer, if failed, give a defaultValue
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int readIntValueWithDefault(String key, int defaultValue) {
		String value = readStringValueWithDefault( key, "" );
		int result = defaultValue;
		try{
			result = Integer.valueOf(value);
		}catch(NumberFormatException e){
		}
		return result;
	}
	
	
	public int readIntValue(String key) {
		String value = readStringValueWithDefault( key, "" );
		int result = 0;
		if(value == null) return result;
		try{
			result = Integer.valueOf(value);
		}catch(NumberFormatException e){
		}
		return result;
	}
	
	
}

