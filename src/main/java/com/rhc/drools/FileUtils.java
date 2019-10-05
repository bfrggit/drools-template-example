package com.rhc.drools;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileUtils {
   private static final Pattern REGEX_PACKAGE_NAME = Pattern.compile("package\\s+([a-zA_Z_][\\.\\w]*)[;\\n\\r]");

   public static byte[] readFileRaw(final String path) throws IOException {
      return Files.readAllBytes(Paths.get(path));
   }

   public static String readFile(final String path, final Charset encoding) throws IOException {
      return new String(readFileRaw(path), encoding);
   }

   public static String parsePackageName(final String file) throws IOException {
      Matcher matcher = REGEX_PACKAGE_NAME.matcher(file);
      List<String> matches = new LinkedList<>();

      while (matcher.find()) {
         matches.add(matcher.group(1));
      }
      if (matches.isEmpty()) {
         throw new IOException("Package name not found in file");
      }
      return matches.get(0);
   }

   private static String buildCsvRow(final Object obj) {
      if (obj instanceof String) {
         return (String) obj;
      } else if (obj instanceof List) {
         return buildCsvRow((List<String>) obj);
      }
      throw new IllegalArgumentException("Unidentifiable row format: " + obj.getClass().getName());
   }

   private static String buildCsvRow(final List<String> fields) {
      return String.join(",", fields.stream().map((final String str) ->
            "\"" + str + "\""
      ).collect(Collectors.toList()));
   }

   public static String buildCsvFile(final List<?> rows, final String eol) {
      return String.join(eol, rows.stream().map((final Object row) ->
            buildCsvRow(row)
      ).collect(Collectors.toList())) + eol;
   }
}
