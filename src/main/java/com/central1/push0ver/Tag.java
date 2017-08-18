package com.central1.push0ver;

import java.io.File;
import java.util.Locale;

import com.atlassian.util.concurrent.Nullable;


/**
 * Created by delliot on 2017-05-31.
 */


public class Tag implements Comparable<Tag> {
    public static final String TAG_IDENTIFIER = "tag: ";

    private final String original;
    private final String rawVersion;
    private final String directory;
    private final Version version;
    private int staleCount;
    private boolean isValid = false;
    private final String projectDir;
    private final MyLogger log;

    public Tag(String projectDir, String tag, MyLogger log) {
        this(projectDir, tag, 0, log);
    }

    public Tag(String projectDir, String tag, int staleCount, MyLogger log) {
        this.log = log;

        // canonicalize to forward-slashes
        projectDir = projectDir.replace('\\', '/');
        if (!"".equals(projectDir) && !projectDir.endsWith("/")) {
            // if non-empty, MUST end with forward-slash
            projectDir = projectDir + "/";
        }

        this.projectDir = projectDir;
        this.original = tag;
        int x = original.lastIndexOf('/');

        // Normally we'd look for x >= 0
        // But we treat "1.2.3" and "/1.2.3" as identical.
        if (x > 0) {
            String dir = original.substring(0, x);
            if (!dir.startsWith("/")) {
                dir = "/" + dir;
            }
            this.directory = dir;
            this.rawVersion = original.length() > x + 1 ? original.substring(x + 1) : "";
        } else {
            this.directory = "";
            this.rawVersion = original;
        }
        if (isValidVersion(rawVersion))
        {
            this.version = new Version(rawVersion);
            this.isValid = true;

        } else {
            this.version = null;
        }
        this.staleCount = staleCount;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean hasSlashes() {
        return original.contains("/");
    }

    public boolean containsSnapshot() { return rawVersion.toUpperCase( Locale.ENGLISH ).contains( "-SNAPSHOT" ); }

    @Override
    public int compareTo(@Nullable Tag o) {
        Version v = ((o != null) ? o.getVersion() : null);
        return VersionComparators.COMPARE_VERSIONS.compare( this.version, v );
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Tag) {
            Tag t = (Tag) o;
            return this.version.equals(t.getVersion()) && this.directory.equals(t.getDirectory());
        } else {
            return false;
        }
    }

    public String getDirectory() {
        return directory;
    }

    public boolean isMaven() {
        String path = projectDir + directory + File.separator + "pom.xml";
        if ("".equals(projectDir) && "".equals(directory)) {
            path = "pom.xml";
        }
        File f = new File( path );
        return f.exists();
    }

    public boolean isNode() {
        String path = projectDir + directory + File.separator + "package.json";
        if ("".equals(projectDir) && "".equals(directory)) {
            path = "package.json";
        }
        File f = new File( path );
        return f.exists();
    }

    public int getStaleCount() {
        return staleCount;
    }

    public Version getVersion() {
        return version;
    }

    private static boolean isValidVersion(String version) {
        char c = version.length() > 0 ? version.charAt( 0 ) : 0;

        // Tolerate tags that start with 'v', e.g., 'v1.2.3'
        if (c == 'v' || c == 'V') {
            if (version.length() > 1) {
                c = version.charAt(1);
            }
        }
        return c >= '0' && c <= '9'; // Character.isDigit() is too complicated.
    }

    public String toString() {
        if ("".equals(directory)) {
            return version.toString();
        } else {
            return this.directory + "/" + this.version.toString();
        }
    }

}
