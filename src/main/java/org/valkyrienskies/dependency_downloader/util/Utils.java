package org.valkyrienskies.dependency_downloader.util;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Utils {

    private enum FeatureStatus {
        UNCHECKED, NOT_AVAILABLE, AVAILABLE
    }

    private static FeatureStatus processHandle = FeatureStatus.UNCHECKED;
    private static Class<?> processHandleClass = null;

    public static boolean canUseProcessHandle() {
        if (processHandle == FeatureStatus.UNCHECKED) {
            try {
                processHandleClass = Class.forName("java.lang.ProcessHandle");
            } catch (ClassNotFoundException e) {
                processHandle = FeatureStatus.NOT_AVAILABLE;
                return false;
            }
            processHandle = FeatureStatus.AVAILABLE;
            return true;
        }

        return processHandle == FeatureStatus.AVAILABLE;
    }

    private static FeatureStatus httpClient;

    public static boolean canUseHttpClient() {
        if (processHandle == FeatureStatus.UNCHECKED) {
            try {
                Class.forName("java.net.http.HttpClient");
            } catch (ClassNotFoundException e) {
                processHandle = FeatureStatus.NOT_AVAILABLE;
                return false;
            }
            processHandle = FeatureStatus.AVAILABLE;
            return true;
        }

        return processHandle == FeatureStatus.AVAILABLE;
    }


    public static void main(String[] args) throws Exception {
        System.out.println();
//        ProcessHandle.Info currentProcessInfo = ProcessHandle.current().info();
//        List<String> newProcessCommandLine = new ArrayList<>();
//        newProcessCommandLine.add(currentProcessInfo.command().get());
//
//        Optional<String[]> currentProcessArgs = currentProcessInfo.arguments();
//
//        System.out.println(getCommandLine(ProcessHandle.current()));
//        System.out.println(currentProcessArgs);
//        System.out.println(currentProcessInfo.commandLine());
    }

    public static Optional<String> guessRestartCommand() {
        if (canUseProcessHandle()) {
            Optional<String> command = getJavaCommandLineJava9();
            if (command.isPresent()) return command;
        }

        if (isWindows()) {
            return Optional.of(getCommandLineWindows());
        }

        return Optional.empty();
    }

    public static String guessJavaCommand() {
        if (canUseProcessHandle()) {
            Optional<String> command = getJavaCommandJava9();
            if (command.isPresent()) return command.get();
        }

        if (isWindows()) {
            return getJVMPathWindows();
        }

        String javaHome = guessJavaCommandFromJavaHome();
        if (javaHome != null) return javaHome;

        throw new RuntimeException("Could not figure out the java command!");
    }

    public static String guessJavaCommandFromJavaHome() {
        String javaHome = System.getProperty("java.home");
        if (javaHome == null) return null;

        if (isWindows()) {
            return javaHome + "\\bin\\java.exe";
        } else {
            return javaHome + "/bin/java";
        }
    }


    public static long getPid() {
        if (canUseProcessHandle()) {
            return getPidJava9();
        } else {
            return getPidJava8();
        }
    }

    public static long getPidJava9() {
        try {
            Object current = getCurrentProcessHandle();
            return (Long) processHandleClass
                .getMethod("pid")
                .invoke(current);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> getJavaCommandJava9() {
        try {
            Object current = getCurrentProcessHandle();
            Object info = processHandleClass
                .getMethod("info")
                .invoke(current);

            Optional<?> command = (Optional<?>) Class.forName("java.lang.ProcessHandle$Info")
                .getMethod("command")
                .invoke(info);

            return (Optional<String>) command;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Optional<String> getJavaCommandLineJava9() {
        try {
            Object current = getCurrentProcessHandle();
            Object info = processHandleClass
                .getMethod("info")
                .invoke(current);

            Optional<?> command = (Optional<?>) Class.forName("java.lang.ProcessHandle$Info")
                .getMethod("commandLine")
                .invoke(info);

            return (Optional<String>) command;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getCurrentProcessHandle() throws Exception {
        return processHandleClass
            .getMethod("current")
            .invoke(null);
    }

    public static long getPidJava8() {
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            Field jvm = runtime.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            Object mgmt = jvm.get(runtime);
            Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
            pid_method.setAccessible(true);

            return (Integer) pid_method.invoke(mgmt);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private static boolean isWindows() {
        return System.getProperty("os.name").contains("Windows");
    }

    // https://www.rgagnon.com/javadetails/java-get-running-jvm-path.html
    public static String getJVMPathWindows() {
        String path = "";
        BufferedReader input = null;
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);

            String vbs = "Set WshShell = WScript.CreateObject(\"WScript.Shell\")\n"
                + "Set locator = CreateObject(\"WbemScripting.SWbemLocator\")\n"
                + "Set service = locator.ConnectServer()\n"
                + "Set processes = service.ExecQuery _\n"
                + " (\"select * from Win32_Process where ProcessId='"
                + getPid() + "'\")\n"
                + "For Each process in processes\n"
                + "wscript.echo process.ExecutablePath \n"
                + "Next\n"
                + "Set WSHShell = Nothing\n";
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            input =
                new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            path = input.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException io) {
                io.printStackTrace();
                return null;
            }
        }
        return path;
    }


    /**
     * Returns the full command-line of the process.
     * <p>
     * This is a workaround for
     * <a href="https://stackoverflow.com/a/46768046/14731">https://stackoverflow.com/a/46768046/14731</a>
     *
     * @return the command-line of the process
     * @throws UncheckedIOException if an I/O error occurs
     */
    private static String getCommandLineWindows() {
        long desiredProcessid = getPid();
        try {
            Process process = new ProcessBuilder("wmic", "process", "where", "ProcessID=" + desiredProcessid, "get",
                "commandline", "/format:list").
                redirectErrorStream(true).
                start();
            try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                 BufferedReader reader = new BufferedReader(inputStreamReader)) {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        throw new IOException("Could not find command-line of process");
                    }
                    if (!line.startsWith("CommandLine=")) {
                        continue;
                    }
                    return line.substring("CommandLine=".length());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
