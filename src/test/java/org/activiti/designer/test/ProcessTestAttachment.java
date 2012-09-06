package org.activiti.designer.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;

public class ProcessTestAttachment {

	private String filename = "/Users/henryyan/work/projects/activiti/activiti-study/src/main/resources/diagrams/Attachment.bpmn";

	@Rule
	public ActivitiRule activitiRule = new ActivitiRule();

	@Test
	public void startProcess() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		TaskService taskService = activitiRule.getTaskService();
		repositoryService.createDeployment().addInputStream("process1.bpmn20.xml", new FileInputStream(filename)).deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process1", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());

		Task singleResult = taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult();
		String url = "http://labs.mop.com/apache-mirror//ant/binaries/apache-ant-1.8.3-bin.zip";
		String attachmentDescription = "ant bin package";
		taskService.createAttachment("zip", singleResult.getId(), processInstance.getId(), "apache-ant-1.8.3-bin.zip",
				attachmentDescription, url);
		taskService.complete(singleResult.getId());

		List<Attachment> taskAttachments = taskService.getTaskAttachments(singleResult.getId());
		assertEquals(1, taskAttachments.size());

		HistoryService historyService = activitiRule.getHistoryService();
		List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();
		assertEquals(false, list.isEmpty());
	}
	
	@Test
	public void testCandidateUsers() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		TaskService taskService = activitiRule.getTaskService();
		repositoryService.createDeployment().addInputStream("process1.bpmn20.xml", new FileInputStream(filename)).deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process1", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());

		assertNotNull(taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult());
		assertNotNull(taskService.createTaskQuery().taskCandidateUser("kafeitu").singleResult());
		
		Task singleResult = taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult();
		taskService.complete(singleResult.getId());
		
		assertNull(taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult());
		assertNull(taskService.createTaskQuery().taskCandidateUser("kafeitu").singleResult());
	}
	
	@Test
	public void testCandidateUsersAddUserRuntime() throws Exception {
		RepositoryService repositoryService = activitiRule.getRepositoryService();
		TaskService taskService = activitiRule.getTaskService();
		repositoryService.createDeployment().addInputStream("process1.bpmn20.xml", new FileInputStream(filename)).deploy();
		RuntimeService runtimeService = activitiRule.getRuntimeService();
		Map<String, Object> variableMap = new HashMap<String, Object>();
		variableMap.put("name", "Activiti");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process1", variableMap);
		assertNotNull(processInstance.getId());
		System.out.println("id " + processInstance.getId() + " " + processInstance.getProcessDefinitionId());

		assertNotNull(taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult());
		assertNotNull(taskService.createTaskQuery().taskCandidateUser("kafeitu").singleResult());
		
		String taskId = taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult().getId();
		taskService.addCandidateUser(taskId, "runtimeUser");
		
		assertNotNull(taskService.createTaskQuery().taskCandidateUser("runtimeUser").singleResult());
		
		Task singleResult = taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult();
		taskService.complete(singleResult.getId());
		
		assertNull(taskService.createTaskQuery().taskCandidateUser("henryyan").singleResult());
		assertNull(taskService.createTaskQuery().taskCandidateUser("kafeitu").singleResult());
	}
	
}