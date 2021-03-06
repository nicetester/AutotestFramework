package luckyweb.seagull.spring.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import luckyweb.seagull.spring.dao.ProjectModuleDao;
import luckyweb.seagull.spring.entity.ProjectModule;
import luckyweb.seagull.util.StrLib;

@Service("projectModuleService")
public class ProjectModuleServiceImpl implements ProjectModuleService{
	
	private ProjectModuleDao projectmoduleDao;
	
	public ProjectModuleDao getProjectModuleDao() {
		return projectmoduleDao;
	}

	@Resource(name = "projectModuleDao")
	public void setProjectModuleDao(ProjectModuleDao projectmoduleDao) {
		this.projectmoduleDao = projectmoduleDao;
	}
	
	@Override
	public int add(ProjectModule projectmodule) throws Exception {
		return this.projectmoduleDao.add(projectmodule);
	}
	
	@Override
	public void delete(ProjectModule projectmodule) throws Exception {
		this.projectmoduleDao.delete(projectmodule);		
	}
	
	@Override
	public void modify(ProjectModule projectmodule) throws Exception {
		this.projectmoduleDao.modify(projectmodule);
		
	}
	
	@Override
	public ProjectModule load(int id) throws Exception {
		// TODO Auto-generated method stub
		return this.projectmoduleDao.load(id);
	}

	private String where(ProjectModule projectmodule) {
		String where = " where ";
		if (projectmodule.getProjectid()!=0&&projectmodule.getProjectid()!=99) {
			where += " projectid =:projectid  and ";
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
		String	hql=" from ProjectModule  "+where((ProjectModule)value)+orderBy;
		List list= projectmoduleDao.findByPage(hql, value, offset, pageSize);
		return list;
	}

	@Override
	public int findRows(ProjectModule projectmodule) {
		String hql="select count(*) from ProjectModule "+where(projectmodule);
		return projectmoduleDao.findRows(projectmodule, hql);
	}
	
	@Override
	public List<ProjectModule> getModuleList(){
		// TODO Auto-generated method stub
		List<ProjectModule> list= null;
		try {
			list=this.projectmoduleDao.getList(" from ProjectModule order by id");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<ProjectModule> getModuleListByProjectid(int projectid,int id){
		// TODO Auto-generated method stub
		List<ProjectModule> list= null;
		try {
			list=this.projectmoduleDao.getList(" from ProjectModule where projectid="+projectid+" and pid="+id+" order by id");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public List<ProjectModule> getModuleAllListByProjectid(int projectid){
		// TODO Auto-generated method stub
		List<ProjectModule> list= null;
		try {
			list=this.projectmoduleDao.getList(" from ProjectModule where projectid="+projectid+" order by id");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	@Override
	public boolean getModuleIsParent(int id){
		// TODO Auto-generated method stub
		boolean result=false;
		try {
			List<ProjectModule> list= null;
			list=this.projectmoduleDao.getList(" from ProjectModule where pid="+id+" order by id");
			if(null!=list&&list.size()>0){
				result=true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public int getModuleIdByName(String modulename) throws Exception {
		// TODO Auto-generated method stub
		String id= this.projectmoduleDao.getModuleIdByName("select IFNULL(MAX(id),0) from project_module where modulename='"+modulename+"'");
		if(!StrLib.isEmpty(id)){
			return Integer.valueOf(id);
		}else{
			return 0;
		}

	}
}
