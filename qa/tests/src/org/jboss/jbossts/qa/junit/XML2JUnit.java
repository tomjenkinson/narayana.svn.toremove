/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * (C) 2009,
 * @author JBoss Inc.
 */
package org.jboss.jbossts.qa.junit;

import org.jboss.dtf.testframework.coordinator2.TestDefinitionRepository;
import org.jboss.dtf.testframework.coordinator2.TestDefinition;
import org.jboss.dtf.testframework.coordinator2.TaskDefinition;
import org.jboss.dtf.testframework.coordinator2.TaskDefinitionRepository;
import org.jboss.dtf.testframework.coordinator.Action;

import java.net.URL;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Utility class that reads DTF -testdef.xml input and writes equivalent JUnit4 tests in .java source format.
 *
 * Usage: java org.jboss.dtf.testframework.junit.XML2JUnit <inputfile> <outputdir>
 *   e.g. java org.jboss.dtf.testframework.junit.XML2JUnit mytests-testdefs-xml /My/Tests/Dir
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2009-05
 */
public class XML2JUnit
{
    public static void main(String[] args) throws Exception {
        String testdefsFile = args[0];
        TestDefinitionRepository testDefinitionRepository = new TestDefinitionRepository(new URL("file://"+(new File(testdefsFile).getAbsolutePath())));
        TaskDefinitionRepository taskDefinitionRepository = new TaskDefinitionRepository(new URL("file://"+(new File(testdefsFile).getAbsolutePath())));

        XML2JUnit instance = new XML2JUnit(testDefinitionRepository, taskDefinitionRepository);

        String classname = instance.generateHeader();

        instance.generateLifecycle();

        instance.generateTests();
        instance.generateFooter();

        FileWriter fileWriter = new FileWriter(args[1]+File.separator+classname+".java");
        fileWriter.write(instance.getBuffer());
        fileWriter.close();
    }


    TestDefinitionRepository testDefinitionRepository = null;
    TaskDefinitionRepository taskDefinitionRepository = null;
    List<TestDefinition> testDefs = null;
    StringBuilder buffer = null;
    int nameCount = 0;

    public XML2JUnit(TestDefinitionRepository testDefinitionRepository, TaskDefinitionRepository taskDefinitionRepository) {
        this.testDefinitionRepository = testDefinitionRepository;
        this.taskDefinitionRepository = taskDefinitionRepository;
        this.buffer = new StringBuilder(512*1024);
    }

    public String getBuffer() {
        return buffer.toString();
    }

    public String generateHeader() {
        Map<String, TestDefinition> testIDs2testDefs = testDefinitionRepository.getTestDefinitionsMap();
        testDefs = new ArrayList(testIDs2testDefs.values());
        Collections.sort(testDefs, new TestDefinitionComparator());

        String name = testDefs.get(0).getId().replace("-", "_").toLowerCase();
        name = name.substring(0, name.lastIndexOf("_"));
        name = "TestGroup_"+name;

        buffer.append("\n");
        buffer.append("package org.jboss.jbossts.qa.junit.generated;\n");
        buffer.append("\n");
        buffer.append("import org.jboss.jbossts.qa.junit.*;\n");
        buffer.append("import org.junit.*;\n");
        buffer.append("\n");
        buffer.append("// Automatically generated by XML2JUnit\n");
        buffer.append("public class "+name+" extends TestGroupBase\n");
        buffer.append("{\n");

        return name;
    }

    public void generateFooter() {
        buffer.append("}");
    }

    public void generateTests() throws Exception {
        for(TestDefinition testDefinition : testDefs) {
            buffer.append("\t@Test public void "+testDefinition.getId().replace("-", "_")+"()\n");
            buffer.append("\t{\n");
            generateTest(testDefinition);
            buffer.append("\t}\n\n");

        }
    }

    private static class TestDefinitionComparator implements Comparator<TestDefinition>
    {
        public int compare(TestDefinition o1, TestDefinition o2)
        {
            return o1.getId().compareTo(o2.getId());
        }
    }

    private TaskDefinition getTaskDef(Action action, TestDefinition testDefinition) throws Exception {
        TaskDefinition taskDefinition = null;
        if(action.getType() == Action.PERFORM_TASK || action.getType() == Action.START_TASK) {
            String groupId = testDefinition.getGroupId();
            String taskIdToPerform	= action.getAssociatedTaskId();
            taskDefinition = taskDefinitionRepository.getTaskDefinition(groupId, taskIdToPerform);
            if (taskDefinition == null)
            {
                taskDefinition = taskDefinitionRepository.getTaskDefinition(taskIdToPerform);
            }
        }
        return taskDefinition;
    }

    // identify required contents of setUp and tearDown
    public void generateLifecycle() throws Exception {

        // EmptyObjectStore is in the test base class, don't generate for it
        for(TestDefinition testDefinition : testDefs) {
            ArrayList<Action> actionList = testDefinition.getActionList();
            TaskDefinition taskDefinition = getTaskDef(actionList.remove(0), testDefinition);
            if(taskDefinition == null || !taskDefinition.getClassName().equals("org.jboss.jbossts.qa.Utils.EmptyObjectStore")) {
                throw new IllegalArgumentException("Test does not start with EmptyObjectStore");
            }
        }

        int startingBufferPosition = buffer.length();

        buffer.append("\n\t@Before public void setUp()\n");
        buffer.append("\t{\n");
        buffer.append("\t\tsuper.setUp();\n");

        List<Action> outstandingActions = generateCommonTasks(false, null); // setUp method

        if(outstandingActions.size() > 0) {
            generateLocalVariables(startingBufferPosition, outstandingActions);
        }

        buffer.append("\t}\n\n");

        buffer.append("\t@After public void tearDown()\n");
        buffer.append("\t{\n");
        // do the per server terminates inside a try blockso we can guarantee to call super.tearDown in
        // a finally block if any of them fails
        buffer.append("\t\ttry {\n");

        generateCommonTasks(true, outstandingActions); // tearDown method

        buffer.append("\t\t} finally {\n");
        buffer.append("\t\t\tsuper.tearDown();\n");
        buffer.append("\t\t}\n");
        buffer.append("\t}\n\n");
    }

    private void generateLocalVariables(int startingBufferPosition, List<Action> actions) throws Exception {
        for(Action action : actions) {
            buffer.insert(startingBufferPosition, "\tprotected Task "+action.getAssociatedRuntimeTaskId()+" = null;\n");
        }
    }

    private List<Action> generateCommonTasks(boolean fromEnd, List<Action> terminationActions) throws Exception {

        if(terminationActions != null) {
            for(Action action : terminationActions) {
                generateTask(action,testDefs.get(0), true);
            }
        }

        TaskDefinition candidateCommonTask;
        List<Action> outstandingActions = new LinkedList<Action>();
        do {
            candidateCommonTask = null;
            for(TestDefinition testDefinition : testDefs) {
                int index = fromEnd ? (testDefinition.getActionList().size()-1) : 0;
                Action action = (Action)testDefinition.getActionList().get(index);
                TaskDefinition taskDef = getTaskDef(action, testDefinition);
                if(taskDef == null) {
                    break;
                }
                if(candidateCommonTask == null
                    && taskDef.getClassName().indexOf(".Server") == -1
                    && taskDef.getClassName().indexOf(".Client") == -1) {
                    candidateCommonTask = taskDef;
                } else if(candidateCommonTask == null) {
                    continue;
                } else if(taskDef.getClassName().equals(candidateCommonTask.getClassName())
                        && taskDef.getParameters().equals(candidateCommonTask.getParameters())) {
                    continue;
                } else {
                    candidateCommonTask = null;
                    break;
                }
            }
            if(candidateCommonTask != null) {

                TestDefinition testDef = testDefs.get(0);
                int index = fromEnd ? (testDef.getActionList().size()-1) : 0;
                Action action = (Action)testDef.getActionList().get(index);
                boolean isSetup = !fromEnd;
                generateTask(action, testDef, isSetup);

                for(TestDefinition testDefinition : testDefs) {
                    index = fromEnd ? (testDefinition.getActionList().size()-1) : 0;
                    testDefinition.getActionList().remove(index);
                }

                if(!fromEnd && action.getType() == Action.START_TASK) {

                    boolean actionAdded = false;
                    for(TestDefinition testDefinition : testDefs) {
                        ArrayList<Action> actionList = testDefinition.getActionList();
                        Iterator<Action> actionIter = actionList.iterator();
                        while(actionIter.hasNext()) {
                            Action candidateTerminatorAction = actionIter.next();
                            if(candidateTerminatorAction.getAssociatedRuntimeTaskId().equals(action.getAssociatedRuntimeTaskId())
                                    && candidateTerminatorAction.getType() == Action.TERMINATE_TASK) {
                                actionIter.remove();
                                if(!actionAdded) {
                                    outstandingActions.add(candidateTerminatorAction);
                                    actionAdded = true;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        } while (candidateCommonTask != null);

        return outstandingActions;
    }

    public String[] filterParams(String[] params) {
        List<String> filteredParams = new ArrayList<String>(params.length);
        for(String param : params) {
            if("$(ORBFLAGS_1)".equals(param)
                    || "$(ORBFLAGS_2)".equals(param)) {
                continue;
            }
            filteredParams.add(param);
        }
        return filteredParams.toArray(new String[filteredParams.size()]);
    }

    public void generateTask(Action action, TestDefinition testDefinition, boolean isSetup) throws Exception {

        TaskDefinition taskDefinition = getTaskDef(action, testDefinition);
        String outputDirectory;
        String filename;
        switch(action.getType()) {
            case Action.PERFORM_TASK:
                String name = (action.getAssociatedRuntimeTaskId() == null ? ("task"+(nameCount++)) : action.getAssociatedRuntimeTaskId());
                outputDirectory = testOutputDirectory(testDefinition, isSetup);
                filename = testOutputFilename(testDefinition, name);
                buffer.append("\t\tTask "+name+" = createTask(");
                buffer.append(taskDefinition.getClassName()+".class, Task.TaskType."+taskDefinition.getTypeText());
                buffer.append(", \"" + outputDirectory + filename + "\", 600);\n");
                if(action.getParameterList().length != 0) {
                    buffer.append("\t\t"+name+".perform("); // new String[] {

                    String[] params = filterParams(action.getParameterList());
                    int length = params.length;
                    for(int i = 0; i < length; i++) {
                        String param = params[i];
                        buffer.append("\""+param+"\"");
                        if(i != length-1) {
                            buffer.append(", ");
                        }
                    }
                    buffer.append(");\n"); // }
                } else {
                    buffer.append("\t\t"+name+".perform();\n");
                }
                break;
            case Action.START_TASK:
                if(isSetup) {
                    buffer.append("\t\t"); // use local variable, don't declate a new one
                } else {
                    buffer.append("\t\tTask ");
                }
                outputDirectory = testOutputDirectory(testDefinition, isSetup);
                filename = testOutputFilename(testDefinition, action.getAssociatedRuntimeTaskId());
                buffer.append(action.getAssociatedRuntimeTaskId() + " = createTask(");
                buffer.append(taskDefinition.getClassName()+".class, Task.TaskType."+taskDefinition.getTypeText());
                buffer.append(", \"" + outputDirectory + filename + "\", 600);\n");
                if(action.getParameterList().length != 0) {
                    buffer.append("\t\t"+action.getAssociatedRuntimeTaskId()+".start("); // new String[] {

                    String[] params = filterParams(action.getParameterList());
                    int length = params.length;
                    for(int i = 0; i < length; i++) {
                        String param = params[i];
                        buffer.append("\""+param+"\"");
                        if(i != length-1) {
                            buffer.append(", ");
                        }
                    }
                    buffer.append(");\n"); // }
                } else {
                    buffer.append("\t\t"+action.getAssociatedRuntimeTaskId()+".start();\n");
                }
                break;
            case Action.TERMINATE_TASK:
                if (isSetup) {
                    buffer.append("\t\t\t"+action.getAssociatedRuntimeTaskId()+".terminate();\n");
                } else {
                    buffer.append("\t\t"+action.getAssociatedRuntimeTaskId()+".terminate();\n");
                }
                break;
            case Action.WAIT_FOR_TASK:
                buffer.append("\t\t"+action.getAssociatedRuntimeTaskId()+".waitFor();\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Action type "+action.getType());
        }
    }

    private String testOutputFilename(TestDefinition testDefinition, String name) {
        // TODO - identify a better naming scheme
        return name + "_output.txt";
    }

    private String testOutputDirectory(TestDefinition testDefinition, boolean isSetup) {
        // TODO - identify a better naming scheme

        String groupId = testDefinition.getGroupId();
        if (isSetup) {
            // setup tasks write their output in the parent directory
            return "./testoutput/" + groupId + "/";
        } else {
            String testId = testDefinition.getId();

            if (testId.startsWith(groupId + "_")) {
                testId = testId.substring(groupId.length() + 1);
            }

            return "./testoutput/" + groupId + "/" + testId.replace("-", "_") + "/";
        }
    }

    public void generateTest(TestDefinition testDefinition) throws Exception {

        if(testDefinition == null) {
            throw new IllegalArgumentException("testDefinition must not be null!");
        }

        if(testDefinition.getNumberOfNodesRequired() != 1) {
            System.err.println("Unsupported node count "+testDefinition.getNumberOfNodesRequired()+" for test "+testDefinition.getDescription()+", skipping it");
            return;
        }

        ArrayList<Action> actionList = testDefinition.getActionList();

        for(Action action : actionList) {
            generateTask(action, testDefinition, false);
        }
    }
}

