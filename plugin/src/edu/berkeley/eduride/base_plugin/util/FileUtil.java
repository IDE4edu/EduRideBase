package edu.berkeley.eduride.base_plugin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class FileUtil {

	public static String getContents(File txtfile) throws FileNotFoundException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(txtfile), "UTF8"));
			String tmp;
			StringBuilder out = new StringBuilder();

			while ((tmp = in.readLine()) != null) {
				out.append(tmp);
				// include the line feed here, sheesh!!!!
				out.append("\n");
			}
			in.close();
			return out.toString();
		} catch (UnsupportedEncodingException e) {
			Console.err(e);
		} catch (IOException e) {
			Console.err(e);
		}
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// already closed?
			}
		}
		return null;
	}
	
}
