package luckyweb.seagull.spring.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import luckyweb.seagull.spring.dao.ProjectPlanDao;
import luckyweb.seagull.spring.entity.ProjectPlan;

@Service("projectPlanService")
public class ProjectPlanServiceImpl implements ProjectPlanService{
	
	private ProjectPlanDao projectplanDao;
	
	public ProjectPlanDao getProjectPlanDao() {
		return projectplanDao;
	}

	@Resource(name = "projectPlanDao")
	public void setProjectPlanDao(ProjectPlanDao projectplanDao) {
		this.projectplanDao = projectplanDao;
	}
	
	@Override
	public int add(ProjectPlan projectplan) throws Exception {
		return this.projectplanDao.add(projectplan);
	}
	
	@Override
	public void delete(ProjectPlan projectplan) throws Exception {
		this.projectplanDao.delete(projectplan);		
	}
	
	@Override
	public void modify(ProjectPlan projectplan) throws Exception {
		this.projectplanDao.modify(projectplan);
		
	}
	
	@Override
	public ProjectPlan load(int id) throws Exception {
		// TODO Auto-generated method stub
		return this.projectplanDao.load(id);
	}

	private String where(ProjectPlan projectplan) {
		String where = " where ";
		if (projectplan.getProjectid()!=0&&projectplan.getProjectid()!=99) {
			where += " projectid =:projectid  and ";
		}if (null!=projectplan.getName()&&!"".equals(projectplan.getName())) {
			where += " (name like :name  or ";
		}if (null!=projectplan.getOperationer()&&!"".equals(projectplan.getOperationer())) {
			where += " operationer like :operationer  or ";
		}if (null!=projectplan.getRemark()&&!"".equals(projectplan.getRemark())) {
			where += " remark like :remark)  or ";
		}
		if (where.length() == 7) {
			where = "";
		} 
		else{
			where = where.substring(0, where.length() - 5);
		}

		return where;
	}
	
	private static String  orderBy=" order by id desc ";
	
	@Override
	public List findByPage(Object value, int offset, int pageSize) {
		String	hql=" from ProjectPlan  "+where((ProjectPlan)value)+orderBy;
		List list= projectplanDao.findByPage(hql, value, offset, pageSize);
		return list;
	}

	@Override
	public int findRows(ProjectPlan projectplan) {
		String hql="select count(*) from ProjectPlan "+where(projectplan);
		return projectplanDao.findRows(projectplan, hql);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProjectPlan getcases(String name) throws Exception {
		// TODO Auto-generated method stub
		List<ProjectPlan> pps=projectplanDao.getList(" from ProjectPlan where name='"+name+"' order by id");
		return pps.get(0);
	}
	
}
