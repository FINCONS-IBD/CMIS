package com.fincons.nlp.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesHelper {

	final static Logger logger = Logger.getLogger(PropertiesHelper.class);

	private static Properties props = new Properties();

	static {

		logger.info("Loading configuration parameters...");

		InputStream input = null;

		/*
		 * String config_file_vm_var = System.getProperty("batch_config_file"); 

			if(config_file_vm_var == null){
				throw new EnvVariableNotFound();
			}else{

				input = new FileInputStream(config_file_vm_var);

				APPLICATION_PROPERTIES.load(input);
			}
		 */

		try {

			String config_file_vm_var = System.getProperty("batch_config_file");
			if(config_file_vm_var == null){
				throw new EnvVariableNotFound();
			}else{
				input = new FileInputStream(config_file_vm_var);
				props.load(input);
			}
		} catch (Exception ex) {
			logger.error("Internal Server Error", ex);
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public static Properties getProps() {
		return props;
	}


}
