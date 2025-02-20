package liquibase.integration.commandline;

import liquibase.Scope;
import liquibase.exception.CommandLineParsingException;
import liquibase.logging.LogMessageFilter;
import liquibase.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;
import java.util.Arrays;
import java.util.List;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.LogRecord;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link Main}
 */
public class MainTest {

    @Test
    public void testCodePointCheck() {
      char badChar = 8192;
      char anotherBadChar = 160;
      Main.CodePointCheck codePointCheck = Main.checkArg("test");
      Assert.assertTrue("This should be a valid string", codePointCheck == null);

      StringBuilder builder = new StringBuilder();
      builder.append(badChar);
      codePointCheck = Main.checkArg(builder.toString());
      Assert.assertTrue("The first character should be invalid",codePointCheck.position == 0);

      builder = new StringBuilder();
      builder.append("A");
      builder.append(badChar);
      codePointCheck = Main.checkArg(builder.toString());
      Assert.assertTrue("The last character should be invalid",codePointCheck.position == builder.length()-1);

      builder = new StringBuilder();
      builder.append("ABC");
      builder.append(anotherBadChar);
      builder.append("DEF");
      int pos = builder.toString().indexOf(anotherBadChar);
      codePointCheck = Main.checkArg(builder.toString());
      Assert.assertTrue("The character in position " + pos + " should be invalid",codePointCheck.position == pos);
    }


    @Test
    public void checkSetup2() {
        Main main = new Main();
        main.command = "snapshot";
        main.url = "jdbc:oracle://localhost:1521/ORCL";
        main.commandParams.add("--outputSchemasAs");
        List<String> messages = main.checkSetup();
        Assert.assertTrue("There should be no messages from Main.checkSetup", messages.size() == 0);

        main.command = "update";
        main.changeLogFile = "changelog.xml";
        messages = main.checkSetup();
        Assert.assertTrue("There should be one message from Main.checkSetup", messages.size() == 1);
    }

//    @Rule
//    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

//    @Mock
//    private CommandFactory commandFactory;
//
//    @Mock
//    private SnapshotCommand snapshotCommand;
//
//    @Mock
//    private SnapshotCommand.SnapshotCommandResult snapshotCommandResult;

    public MainTest() throws Exception {
//        PowerMockito.mockStatic(CommandFactory.class);
//
//        commandFactory = PowerMockito.mock(CommandFactory.class);
//        snapshotCommand = PowerMockito.mock(SnapshotCommand.class);
//        snapshotCommandResult = PowerMockito.mock(SnapshotCommand.SnapshotCommandResult.class);
//
//        // Do not do actual database snapshots.
//        when(Scope.getCurrentScope().getSingleton(CommandFactory.class)).thenReturn(commandFactory);
//        when(commandFactory.getCommand("snapshot")).thenReturn(snapshotCommand);
//        when(snapshotCommand.execute()).thenReturn(snapshotCommandResult);
//        when(snapshotCommandResult.print()).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?>...");
//
//        // This one is not so much for JUnit, but for people working with IntelliJ. It seems that IntelliJ's
//        // test runner can get confused badly if tests open an OutputStreamWriter in STDOUT.
//        PowerMockito.stub(method(Main.class, "getOutputWriter"))
//                .toReturn(new OutputStreamWriter(System.err));

    }

    @Test
    public void testLocalProperties() throws Exception {

        String[] args = new String[]{
                "--driver=DRIVER",
                "--username=USERNAME",
                "--password=PASSWORD",
                "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" +
                        "&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" +
                        "&sendsStringParametersAsUnicode=true",
                "--changeLogFile=dummy.log",
                "--changeExecListenerClass=MockChangeExecListener",
                "--defaultsFile=target/test-classes/liquibase.properties",
                "snapshot"
        };

        Main cli = new Main();
        cli.parseOptions(args);

//        assertTrue("Read context from liquibase.local.properties", ((cli.contexts != null) && cli.contexts.contains
//            ("local-context-for-liquibase-unit-tests")));
        assertTrue("Read context from liquibase.properties", ((cli.logFile != null) && ("target" +
            "/logfile_set_from_liquibase_properties.log").equals(cli.logFile)));
    }

    @Test
    public void testSecureLogFilterForNullLogMessage() throws Exception {
        LogMessageFilter mockFilter = mock(LogMessageFilter.class);
        when(mockFilter.filterMessage(null)).thenReturn(null);
        Main.SecureLogFilter secureLogFilter = new Main.SecureLogFilter(mockFilter);

        LogRecord mockLogRecord = mock(LogRecord.class);
        when(mockLogRecord.getMessage()).thenReturn(null);
        assertFalse(secureLogFilter.isLoggable(mockLogRecord));
    }

    @Test
    public void startWithoutParameters() throws Exception {
//        exit.expectSystemExitWithStatus(1);
        Main.run(new String[0]);
        assertTrue("We just want to survive until this point", true);
    }

    @Test
    public void globalConfigurationSaysDoNotRun() throws Exception {
        Scope.child(Collections.singletonMap(LiquibaseCommandLineConfiguration.SHOULD_RUN.getKey(), false), () -> {

            int errorLevel = Main.run(new String[0]);
            assertEquals(errorLevel, 0); // If it SHOULD run, and we would call without parameters, we would get -1
        });
    }

//    @Test
//    public void mockedSnapshotRun() throws Exception {
//        String[] args = new String[]{
//                "--driver=DRIVER",
//                "--username=USERNAME",
//                "--password=PASSWORD",
//                "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" +
//                        "&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" +
//                        "&sendsStringParametersAsUnicode=true",
//                "--changeLogFile=dummy.log",
//                "--changeExecListenerClass=MockChangeExecListener",
//                "snapshot",
//        };
//        int errorLevel = Main.run(args);
//        assertEquals(0, errorLevel);
//    }

//    @Test
//    public void localPropertyFiles() throws Exception {
//        String[] args = new String[]{
//                "--driver=DRIVER",
//                "--username=USERNAME",
//                "--password=PASSWORD",
//                "--url=offline:mock?version=1.20&productName=SuperDuperDatabase&catalog=startCatalog" +
//                        "&caseSensitive=true&changeLogFile=liquibase/database/simpleChangeLog.xml" +
//                        "&sendsStringParametersAsUnicode=true",
//                "--changeLogFile=dummy.log",
//                "--changeExecListenerClass=MockChangeExecListener",
//                "snapshot",
//        };
//        int errorLevel = Main.run(args);
//        assertEquals(0, errorLevel);
//    }

    @Test
    public void migrateWithAllParameters() throws Exception {
        String[] args = new String[]{
                "--driver=DRIVER",
                "--username=USERNAME",
                "--password=PASSWORD",
                "--url=URL",
                "--changeLogFile=FILE",
                "--classpath=CLASSPATH;CLASSPATH2",
                "--contexts=CONTEXT1,CONTEXT2",
                "--promptForNonLocalDatabase=true",
                "--changeExecListenerClass=MockChangeExecListener",
                "--changeExecListenerPropertiesFile=PROPS",
                "update",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Option --driver was parsed correctly", "DRIVER", cli.driver);
        assertEquals("Option --username was parsed correctly", "USERNAME", cli.username);
        assertEquals("Option --password was parsed correctly", "PASSWORD", cli.password);
        assertEquals("Option --url was parsed correctly", "URL", cli.url);
        assertEquals("Option --changeLogFile was parsed correctly", "FILE", cli.changeLogFile);
        assertEquals("Option --classpath was parsed correctly", "CLASSPATH;CLASSPATH2", cli.classpath);
        assertEquals("Option --contexts was parsed correctly", "CONTEXT1,CONTEXT2", cli.contexts);
        assertEquals("Option --promptForNonLocalDatabase was parsed correctly", Boolean.TRUE,
                cli.promptForNonLocalDatabase);
        assertEquals("Main command 'update' was parsed correctly", "update", cli.command);
        assertEquals("Option --changeExecListenerClass was parsed correctly", "MockChangeExecListener", cli
                .changeExecListenerClass);
        assertEquals("Option --changeExecListenerPropertiesFile was parsed correctly", "PROPS", cli
                .changeExecListenerPropertiesFile);
    }

    @Test
    public void falseBooleanParameters() throws Exception {
        String[] args = new String[]{
                "--promptForNonLocalDatabase=false",
                "update",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Option --promptForNonLocalDatabase=false was parsed correctly", Boolean.FALSE, cli
                .promptForNonLocalDatabase);
        assertEquals("Main command 'update' was parsed correctly", "update", cli.command);
    }

    @Test
    public void convertMigrateToUpdate() throws Exception {
        String[] args = new String[]{
                "--promptForNonLocalDatabase=false",
                "migrate",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Option --promptForNonLocalDatabase was parsed correctly",
                Boolean.FALSE, cli.promptForNonLocalDatabase);
        assertEquals("Main command 'migrate' was parsed correctly as 'update'", "update", cli.command);
    }

    @Test
    public void trueBooleanParameters() throws Exception {
        String[] args = new String[]{
                "--promptForNonLocalDatabase=true",
                "update",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Option --promptForNonLocalDatabase=true was parsed correctly",
                Boolean.TRUE, cli.promptForNonLocalDatabase);
        assertEquals("Main command 'update' was parsed correctly", "update", cli.command);

    }

    @Test(expected = CommandLineParsingException.class)
    public void parameterWithoutDash() throws Exception {
        String[] args = new String[]{
                "promptForNonLocalDatabase=true",
                "update",
        };

        Main cli = new Main();
        cli.parseOptions(args);
    }

    @Test
    public void emptyUrlParameter() throws Exception {
        String[] args = new String[]{
                "--changeLogFile=FILE",
                "--url=",
                "update",
        };

        Main cli = new Main();
        cli.parseOptions(args);
        List<String> errMsgs = cli.checkSetup();
        assertEquals("specifying an empty URL should return 1 error message.", 1, errMsgs.size());
    }

    @Test
    public void misplacedDiffTypesDataOption() throws Exception {
        String[] args = new String[]{
                "--changeLogFile=FILE",
                "--url=TESTFILE",
                "diffChangeLog",
                "--diffTypes=data"
        };

        Main cli = new Main();
        cli.parseOptions(args);
        List<String> errMsgs = cli.checkSetup();
        assertEquals("the combination of --diffTypes=data and diffChangeLog must not be accepted.", 1, errMsgs.size());
    }



    @Test(expected = CommandLineParsingException.class)
    public void unknownParameter() throws Exception {
        String[] args = new String[]{
                "--promptForNonLocalDatabase=true",
                "--badParam=here",
                "migrate",
        };

        Main cli = new Main();
        cli.parseOptions(args);
    }

    @Test
    public void statusVerbose() throws Exception {
        String[] args = new String[]{
                "--url=URL",
                "--changeLogFile=FILE",
                "status",
                "--verbose",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);

        List<String> errMsgs = cli.checkSetup();
        assertEquals(0,errMsgs.size()); // verbose option parsed correctly
    }

    @Test
    public void statusVerboseWithValue() throws Exception {
        String[] args = new String[]{
                "--url=URL",
                "--changeLogFile=FILE",
                "status",
                "--verbose=true",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);

        List<String> errMsgs = cli.checkSetup();
        assertEquals(1,errMsgs.size()); // value is not expected and will raise an error message

    }


    @Test
    public void statusWithoutVerbose() throws Exception {
        String[] args = new String[]{
                "--url=URL",
                "--changeLogFile=FILE",
                "status",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Main command 'status' was not correctly parsed", "status", cli.command);

        List<String> errMsgs = cli.checkSetup();
        assertEquals(0,errMsgs.size());
    }


    @Test(expected = CommandLineParsingException.class)
    public void configureNonExistantClassloaderLocation() throws Exception {
        Main cli = new Main();
        cli.classpath = "badClasspathLocation";
        cli.configureClassLoader();
    }

    @Test
    public void windowsConfigureClassLoaderLocation() throws Exception {
        Main cli = new Main();

        if (cli.isWindows())
        {
          System.setProperty("os.name", "Windows XP");
          cli.classpath = "c:\\;c:\\windows\\";
          cli.applyDefaults();
          cli.configureClassLoader();

          URL[] classloaderURLs = ((URLClassLoader) cli.classLoader).getURLs();
            assertEquals("Parsing example Windows classpath returns 2 entries", 2, classloaderURLs.length);
            assertEquals("Windows path C:\\ is correctly parsed", "file:/c:/", classloaderURLs[0].toExternalForm());
            assertEquals("Windows path C:\\windows\\ is correctly parsed", "file:/c:/windows/", classloaderURLs[1].toExternalForm());
        }
    }

    @Test
    public void unixConfigureClassLoaderLocation() throws Exception {
        Main cli = new Main();

        if (!cli.isWindows())
        {
          System.setProperty("os.name", "Linux");
          cli.classpath = "/tmp:/";
          cli.applyDefaults();

          cli.configureClassLoader();

          URL[] classloaderURLs = ((URLClassLoader) cli.classLoader).getURLs();
          assertEquals(2, classloaderURLs.length);
          assertEquals("file:/tmp/", classloaderURLs[0].toExternalForm());
          assertEquals("file:/", classloaderURLs[1].toExternalForm());
        }
    }

    @Test
    public void propertiesFileWithNoOtherArgs() throws Exception {
        Main cli = new Main();

        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("username", "USERNAME");
        props.setProperty("password", "PASSWD");
        props.setProperty("url", "URL");
        props.setProperty("changeLogFile", "FILE");
        props.setProperty("classpath", "CLASSPAHT");
        props.setProperty("contexts", "CONTEXTS");
        props.setProperty("promptForNonLocalDatabase", "TRUE");

        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");

        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));

        assertEquals("DRIVER", cli.driver);
        assertEquals("USERNAME", cli.username);
        assertEquals("PASSWD", cli.password);
        assertEquals("URL", cli.url);
        assertEquals("FILE", cli.changeLogFile);
        assertEquals("CLASSPAHT", cli.classpath);
        assertEquals("CONTEXTS", cli.contexts);
        assertEquals(Boolean.TRUE, cli.promptForNonLocalDatabase);

    }

    @Test
    public void propertiesFileWithOtherArgs() throws Exception {
        Main cli = new Main();
        cli.username = "PASSED USERNAME";
        cli.password = "PASSED PASSWD";


        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("username", "USERNAME");
        props.setProperty("password", "PASSWD");
        props.setProperty("url", "URL");
        props.setProperty("changeLogFile", "FILE");
        props.setProperty("classpath", "CLASSPAHT");
        props.setProperty("contexts", "CONTEXTS");
        props.setProperty("promptForNonLocalDatabase", "TRUE");

        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");

        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));

        assertEquals("DRIVER", cli.driver);
        assertEquals("PASSED USERNAME", cli.username);
        assertEquals("PASSED PASSWD", cli.password);
        assertEquals("URL", cli.url);
        assertEquals("FILE", cli.changeLogFile);
        assertEquals("CLASSPAHT", cli.classpath);
        assertEquals("CONTEXTS", cli.contexts);
        assertEquals(Boolean.TRUE, cli.promptForNonLocalDatabase);

    }

    @Test
    public void propertiesFileChangeLogParameters() throws Exception {
        Main cli = new Main();

        Properties props = new Properties();
        props.setProperty("driver", "DRIVER");
        props.setProperty("parameter.some_changelog_parameter", "parameterValue");

        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");

        cli.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));

        assertEquals("Changelog parameter in properties file is recognized", "parameterValue",
            cli.changeLogParameters.get("some_changelog_parameter"));

    }

    @Test
    public void applyDefaults() {
        Main cli = new Main();

        cli.promptForNonLocalDatabase = Boolean.TRUE;
        cli.applyDefaults();
        assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.TRUE, cli.promptForNonLocalDatabase);

        cli.promptForNonLocalDatabase = Boolean.FALSE;
        cli.applyDefaults();
        assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.FALSE, cli.promptForNonLocalDatabase);

        cli.promptForNonLocalDatabase = null;
        cli.applyDefaults();
        assertEquals("Correct default value for --promptForNonLocalDatabase", Boolean.FALSE, cli.promptForNonLocalDatabase);

    }

    @Test
    public void checkSetup() {
        Main cli = new Main();
        assertTrue(!cli.checkSetup().isEmpty());

        cli.driver = "driver";
        cli.username = "username";
        cli.password = "pwd";
        cli.url = "url";
        cli.changeLogFile = "file";
        cli.classpath = "classpath";

        assertTrue(!cli.checkSetup().isEmpty());

        cli.command = "BadCommand";
        assertTrue(!cli.checkSetup().isEmpty());

        cli.command = "migrate";
        assertEquals(0, cli.checkSetup().size());

        String[] noArgCommand = {"migrate", "migrateSQL", "update", "updateSQL",
                "updateTestingRollback", "listLocks",
                "releaseLocks", "validate", "help",
                "clearCheckSums", "changelogSync", "changelogSyncSQL"
        };

        cli.commandParams.clear();
        cli.commandParams.add("--logLevel=debug");

        // verify unexpected parameter
        for (int i = 0; i < noArgCommand.length; i++) {
            cli.command = noArgCommand[i];
            assertEquals("Command " + cli.command, 1, cli.checkSetup().size());
        }

        // test update cmd with -D parameter
        cli.command = "update";
        cli.commandParams.clear();
        cli.changeLogParameters.clear();
        cli.changeLogParameters.put("engine", "myisam");
        assertEquals(0, cli.checkSetup().size());

        // verify normal case - comand w/o command parameters
        cli.commandParams.clear();
        for (int i = 0; i < noArgCommand.length; i++) {
            cli.command = noArgCommand[i];
            assertEquals(0, cli.checkSetup().size());
        }

        String[] singleArgCommand = {"updateCount", "updateCountSQL",
                "tag", "dbDoc"
        };

        // verify unexpected parameter for single arg commands
        cli.commandParams.add("--logLevel=debug");
        for (int i = 0; i < singleArgCommand.length; i++) {
            cli.command = singleArgCommand[i];
            assertEquals(1, cli.checkSetup().size());
        }

        // verify normal case - comand with string command parameter
        cli.commandParams.clear();
        cli.commandParams.add("someCommandValue");
        for (int i = 0; i < singleArgCommand.length; i++) {
            cli.command = singleArgCommand[i];
            assertEquals(0, cli.checkSetup().size());
        }

        // status w/o parameter
        cli.command = "status";
        cli.commandParams.clear();
        assertEquals(0, cli.checkSetup().size());

        // status w/--verbose
        cli.commandParams.add("--verbose");
        assertEquals(0, cli.checkSetup().size());

        cli.commandParams.clear();
        cli.commandParams.add("--logLevel=debug");
        assertEquals(1, cli.checkSetup().size());

        String[] multiArgCommand = {"diff", "diffChangeLog"};

        //first verify diff cmds w/o args
        cli.commandParams.clear();
        for (int i = 0; i < multiArgCommand.length; i++) {
            cli.command = multiArgCommand[i];
            assertEquals(0, cli.checkSetup().size());
        }

        // next verify with all parms
        String[] cmdParms = {"--referenceUsername=USERNAME", "--referencePassword=PASSWORD",
                "--referenceUrl=URL", "--referenceDriver=DRIVER"};
        // load all parms
        for (String param : cmdParms) {
            cli.commandParams.add(param);
        }
        assertEquals(0, cli.checkSetup().size());

        // now add an unexpected parm
        cli.commandParams.add("--logLevel=debug");
        assertEquals(1, cli.checkSetup().size());
    }

    @Test
    public void tag() throws Exception {
        String[] args = new String[]{
                "--driver=DRIVER",
                "--username=USERNAME",
                "--password=PASSWORD",
                "--url=URL",
                "--changeLogFile=FILE",
                "--classpath=CLASSPATH;CLASSPATH2",
                "--contexts=CONTEXT1,CONTEXT2",
                "--databaseChangeLogTablespaceName=MYTABLES",
                "tag", "TagHere"
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals("Command line option --driver is parsed correctly", "DRIVER", cli.driver);
        assertEquals("Command line option --username is parsed correctly", "USERNAME", cli.username);
        assertEquals("Command line option --password is parsed correctly", "PASSWORD", cli.password);
        assertEquals("Command line option --url is parsed correctly", "URL", cli.url);
        assertEquals("Command line option --changeLogFile is parsed correctly", "FILE", cli.changeLogFile);
        assertEquals("Command line option --classpath is parsed correctly", "CLASSPATH;CLASSPATH2", cli.classpath);
        assertEquals("Command line option --contexts is parsed correctly", "CONTEXT1,CONTEXT2", cli.contexts);
        assertEquals("Command line option --databaseChangeLogTablespaceName is parsed correctly", "MYTABLES", cli.databaseChangeLogTablespaceName);
        assertEquals("Main command 'tag' is parsed correctly", "tag", cli.command);
        assertEquals("Command parameter 'TagHere' is parsed correctly", "TagHere", cli.commandParams.iterator().next());
    }

    @Test
    public void migrateWithEqualsInParams() throws Exception {
        String url = "dbc:sqlserver://127.0.0.1;DatabaseName=dev_nn;user=ffdatabase;password=p!88worD";
        String[] args = new String[]{
                "--url=" + url,
                "migrate",
        };

        Main cli = new Main();
        cli.parseOptions(args);

        assertEquals(url, cli.url);
    }

    @Test
    public void fixArgs() {
        Main liquibase = new Main();
        String[] fixedArgs = liquibase.fixupArgs(new String[]{"--defaultsFile", "liquibase.properties", "migrate"});
        assertEquals("--defaultsFile=liquibase.properties migrate",
                StringUtil.join(Arrays.asList(fixedArgs), " "));

        fixedArgs = liquibase.fixupArgs(new String[] {"--defaultsFile=liquibase.properties", "migrate"});
        assertEquals("--defaultsFile=liquibase.properties migrate",
                StringUtil.join(Arrays.asList(fixedArgs), " "));

        fixedArgs = liquibase.fixupArgs(new String[] {"--driver=DRIVER",
                "--username=USERNAME",
                "--password=PASSWORD",
                "--url=URL",
                "--changeLogFile=FILE",
                "--classpath=CLASSPATH;CLASSPATH2",
                "--contexts=CONTEXT1,CONTEXT2",
                "--promptForNonLocalDatabase=true",
                "migrate"
        });
        assertEquals("--driver=DRIVER --username=USERNAME --password=PASSWORD --url=URL --changeLogFile=FILE " +
                "--classpath=CLASSPATH;CLASSPATH2 --contexts=CONTEXT1,CONTEXT2 " +
                "--promptForNonLocalDatabase=true migrate", StringUtil.join(Arrays.asList(fixedArgs), " "));
    }

    @Test
    public void testVersionArg() throws Exception {
        Main.run(new String[] {"--version"});
        assertTrue(true); // Just want to test if the call goes through
    }

	@Test
	public void testSplitArgWithValueEndingByEqualSing() throws CommandLineParsingException {
		final String argName = "password";
		final String argValue = "s3-cr3t=";
		Main tested = new Main();

		tested.parseOptions(new String[] { "--" + argName + "=" + argValue });

        assertEquals("Password containing an equal sign (=) is parsed correctly", argValue, tested.password);
    }

    @Test
    public void testDatabaseChangeLogTableName_Properties() throws IOException, CommandLineParsingException {
        Main main = new Main();
        Properties props = new Properties();
        props.setProperty("databaseChangeLogTableName", "PROPSCHANGELOG");
        props.setProperty("databaseChangeLogLockTableName", "PROPSCHANGELOGLOCK");
        ByteArrayOutputStream propFile = new ByteArrayOutputStream();
        props.store(propFile, "");
        main.parsePropertiesFile(new ByteArrayInputStream(propFile.toByteArray()));

        assertEquals("Custom database change log table gets parsed correctly (as a property)", "PROPSCHANGELOG", main
                .databaseChangeLogTableName);
        assertEquals("Custom database change log LOCK table gets parsed correctly (as a property)", "PROPSCHANGELOGLOCK", main.databaseChangeLogLockTableName);
    }

    @Test
    public void testDatabaseChangeLogTableName_Options() throws CommandLineParsingException {
        Main main = new Main();
        String[] opts = {
                "--databaseChangeLogTableName=OPTSCHANGELOG",
                "--databaseChangeLogLockTableName=OPTSCHANGELOGLOCK"};
        main.parseOptions(opts);
        assertEquals("Custom database change log table gets parsed correctly (as an option argument)",
                "OPTSCHANGELOG", main.databaseChangeLogTableName);
        assertEquals("Custom database change log LOCK table gets parsed correctly (as an option argument)", "OPTSCHANGELOGLOCK", main.databaseChangeLogLockTableName);
    }
}
