package io.gamioo.sandbox.util;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

/**
 * 文件操作工具类.
 *
 * @author Allen Jiang
 */
public class FileUtils {
    private static final Logger logger = LogManager.getLogger(FileUtils.class);
    private static final String URL_PROTOCOL_JAR = "jar";
    /**
     * 可读大小的单位
     */
    private static final String[] UNITS = new String[]{"B", "KB", "MB", "GB", "TB", "EB"};






    /**
     * 读取指定名称文件中的文本.
     * TODO(fix): 当fileName不存在的情况下，会导致空指针异常。
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static File getFile(String fileName) {
        // 通过url获取File的绝对路径
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileName);
        if (url != null) {
            return new File(url.getFile());
        } else {
            return null;
        }


    }

    /**
     * 读取指定名称文件中的文本.
     * 获取jar包内的资源
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static File getFileFromJar(String fileName) {
        // 通过url获取File的绝对路径
        URL url = FileUtils.class.getResource(fileName);
        if (url != null) {
            return new File(url.getFile());
        } else {
            return null;
        }
    }


    /**
     * 读取指定名称文件中的文本.
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     */
    public static InputStream getInputStream(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建，默认是覆盖原文件
     *
     * @param fileName 文件名称
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, String content) throws IOException {
        writeFileText(fileName, false, content);
    }

    /**
     * 写入指定文本到文件中.
     * <p>
     * 文件不存在，则会自动创建
     *
     * @param fileName 文件名称
     * @param append   是否追加写入
     * @param content  要写入的内容
     * @throws IOException If an I/O error occurs
     */
    public static void writeFileText(String fileName, boolean append, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName, append); OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            osw.write(content);
            osw.flush();
        }
    }

    /**
     * 可读的文件大小
     *
     * @param file 文件
     * @return 大小
     */
    public static String readableFileSize(File file) {
        return readableFileSize(file.length());
    }

    /**
     * 可读的文件大小<br>
     *
     * @param size Long类型大小
     * @return 大小
     */
    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + UNITS[digitGroups];
    }

    /**
     * Reads the contents of a file line by line to a List of Strings.
     * The file is always closed.
     *
     * @param file    the file to read, must not be {@code null}
     * @param charset the charset to use, {@code null} means platform default
     * @return the list of Strings representing each line in the file, never {@code null}
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static List<String> readLines(final File file, final Charset charset) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtils.readLines(in, Charsets.toCharset(charset));
        }
    }

    /**
     * Opens a {@link FileInputStream} for the specified file, providing better
     * error messages than simply calling <code>new FileInputStream(file)</code>.
     * <p>
     * At the end of the method either the stream will be successfully opened,
     * or an exception will have been thrown.
     * </p>
     * <p>
     * An exception is thrown if the file does not exist.
     * An exception is thrown if the file object exists but is a directory.
     * An exception is thrown if the file exists but cannot be read.
     * </p>
     *
     * @param file the file to open for input, must not be {@code null}
     * @return a new {@link FileInputStream} for the specified file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if the file object is a directory
     * @throws IOException           if the file cannot be read
     * @since 1.3
     */
    public static FileInputStream openInputStream(final File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead()) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileInputStream(file);
    }


    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file        the file to read, must not be {@code null}
     * @param charsetName the name of the requested charset, {@code null} means platform default
     * @return the file contents, never {@code null}
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static String readFileToString(final File file, final Charset charsetName) throws IOException {
        try (InputStream in = openInputStream(file)) {

            return IOUtils.toString(in, Charsets.toCharset(charsetName));
        }
    }

    /**
     * 加载类路径下指定名称文件中的文本. 包括jar 里的还是在jar外的resource
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     * @throws IOException 文件不存在
     */
    public static byte[] getByteArrayFromFile(String fileName) throws IOException {
        byte[] ret;
        URL url = ClassUtils.getDefaultClassLoader().getResource(fileName);
        File file = null;
        if (url != null && StringUtils.equals(url.getProtocol(), URL_PROTOCOL_JAR)) {
            // file = new File(url.getFile());
            ret = getByteArrayFromJar(fileName);
        } else {
            file = getFile(fileName);
            ret = FileUtils.readFileToByteArray(file);

        }


        return ret;

    }

    /**
     * Reads the contents of a file into a String.
     * The file is always closed.
     *
     * @param file the file to read, must not be {@code null}
     * @return the file contents, never {@code null}
     * @throws IOException in case of an I/O error
     * @since 2.3
     */
    public static byte[] readFileToByteArray(final File file) throws IOException {
        try (InputStream in = openInputStream(file)) {
            return IOUtils.toByteArray(in);
        }
    }

    /**
     * 读取指定名称文件中的文本.
     * 获取jar包内的资源
     *
     * @param fileName 文件名称
     * @return 返回文件中的文本
     * @throws IOException 文件不存在
     */
    public static byte[] getByteArrayFromJar(String fileName) throws IOException {
        // 通过url获取File的绝对路径
        //  InputStream input = FileUtils.class.getResourceAsStream(fileName);
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if (input != null) {
            return IOUtils.toByteArray(input);
        } else {
            throw new IOException(fileName + " not exist");
        }


    }


}