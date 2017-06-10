package com.github.sardine.ant.command;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import com.github.sardine.Sardine;
import com.github.sardine.ant.Command;


/**
 * A nice ant wrapper around sardine.get().
 *
 * @author Peter Johnson
 */
public class Get extends Command {

	/** The source URL as a string. */
	private String urlString;

	/** The parsed source URL. */
	private URL source;

	/** A single destination file. */
	private String destFile;

	/** Whether it is okay to overwrite an existing destination file. */
	private boolean overwrite = false;

	/** A single destination property. */
	private String destProperty;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void execute() throws Exception {
		long fileCounter = 0;

		File f = null;
		if (destFile != null) {
			f = new File(destFile);
			if (f.exists() && !overwrite) {
				log("skipping " + source);
				return;
			}
		}

		log("getting " + source);
		InputStream is = getSardine().get(source.toString());

		byte buf[] = new byte[1024];
		int len;
		if (destProperty != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
				out.flush();
			}
			String s = out.toString("UTF-8");
			getProject().setNewProperty(destProperty, s);
			out.close();
		} else {
			FileOutputStream out = new FileOutputStream(f);
			while ((len = is.read(buf)) > 0) {
				out.write(buf, 0, len);
				out.flush();
			}
			out.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateAttributes() throws Exception {
		if (urlString == null)
			throw new NullPointerException("url must not be null");
		source = new URL(urlString);

		if (destFile == null && destProperty == null)
			throw new NullPointerException("Need to define either the file attribute or the property attribute.");
	}

	/** Set source URL. */
	public void setUrl(String urlString) {
		this.urlString = urlString;
	}

	/** Set destination file. */
	public void setFile(String file) {
		this.destFile = file;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void setProperty(String property) {
		this.destProperty = property;
	}
}
