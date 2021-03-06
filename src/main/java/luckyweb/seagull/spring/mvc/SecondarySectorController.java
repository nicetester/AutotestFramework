package luckyweb.seagull.spring.mvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import luckyweb.seagull.spring.entity.SecondarySector;
import luckyweb.seagull.spring.service.OperationLogService;
import luckyweb.seagull.spring.service.SecondarySectorService;
import luckyweb.seagull.spring.service.SectorProjectsService;
import luckyweb.seagull.util.StrLib;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/secondarySector")
public class SecondarySectorController {
	
	@Resource(name = "secondarysectorService")
	private SecondarySectorService secondarysectorservice;

	@Resource(name = "operationlogService")
	private OperationLogService operationlogservice;
	
	@Resource(name = "sectorprojectsService")
	private SectorProjectsService sectorprojectsservice;
	/**
	 * 
	 * 
	 * @param tj
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/load.do")
	public String load(HttpServletRequest req, Model model) throws Exception {
		return "/jsp/base/secondarysector";
	}
	
	@RequestMapping(value = "/list.do")
	private void ajaxGetSellRecord(Integer limit, Integer offset, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();
		String search = request.getParameter("search");
		SecondarySector ss=new SecondarySector();
		if (null == offset && null == limit) {
			offset = 0;
		}
		if (null == limit || limit == 0) {
			limit = 10;
		}
		// 得到客户端传递的查询参数
		if (!StrLib.isEmpty(search)) {
		    ss.setDepartmentname(search);
		}

		List<SecondarySector> sectors = secondarysectorservice.findByPage(ss, offset, limit);
		
		// 转换成json字符串
		String RecordJson = StrLib.listToJson(sectors);
		// 得到总记录数
		int total = secondarysectorservice.findRows(ss);
		// 需要返回的数据有总记录数和行数据
		JSONObject json = new JSONObject();
		json.put("total", total-1);
		json.put("rows", RecordJson);
		pw.print(json.toString());
	}
	
	@RequestMapping(value = "/update.do")
	public void update(HttpServletRequest req, HttpServletResponse rsp, SecondarySector sectors) {
		// 更新实体
		try {
			rsp.setContentType("text/html;charset=utf-8");
			req.setCharacterEncoding("utf-8");
			PrintWriter pw = rsp.getWriter();
			JSONObject json = new JSONObject();
			if (!UserLoginController.permissionboolean(req, "dpmt_3")) {
				json.put("status", "fail");
				json.put("ms", "当前用户无权限修改部门信息，请联系管理员！");
			} else {				
				secondarysectorservice.modify(sectors);

				operationlogservice.add(req, "SecondarySector", sectors.getSectorid(), 
						99,"部门信息修改成功！部门名称："+sectors.getDepartmentname());
				
				json.put("status", "success");
				json.put("ms", "编辑部门成功!");
			}
			pw.print(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 添加部门
	 * 
	 * @param tj
	 * @param br
	 * @param model
	 * @param req
	 * @param rsp
	 * @return
	 * @throws Exception
	 * @Description:
	 */
	@RequestMapping(value = "/add.do")
	public void add(SecondarySector sectors, HttpServletRequest req, HttpServletResponse rsp) throws Exception {
		try {
			rsp.setContentType("text/html;charset=utf-8");
			req.setCharacterEncoding("utf-8");
			PrintWriter pw = rsp.getWriter();
			JSONObject json = new JSONObject();
			if (!UserLoginController.permissionboolean(req, "dpmt_1")) {
				json.put("status", "fail");
				json.put("ms", "增加部门失败,权限不足,请联系管理员!");
			} else {
				int sid = secondarysectorservice.add(sectors);
				operationlogservice.add(req, "SectorProjects", sid, 
						99,"部门添加成功！部门名："+sectors.getDepartmentname());
				
				json.put("status", "success");
				json.put("ms", "添加部门成功!");
			}
			pw.print(json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 删除项目
	 * 
	 * @param tj
	 * @param br
	 * @param model
	 * @param req
	 * @param rsp
	 * @return
	 * @throws Exception
	 * @Description:
	 */
	@RequestMapping(value = "/delete.do")
	public void delete(HttpServletRequest req, HttpServletResponse rsp) throws Exception {
		try {
			rsp.setContentType("text/html;charset=utf-8");
			req.setCharacterEncoding("utf-8");
			PrintWriter pw = rsp.getWriter();
			JSONObject json = new JSONObject();
			if (!UserLoginController.permissionboolean(req, "dpmt_2")) {
				json.put("status", "fail");
				json.put("ms", "删除部门失败,权限不足,请联系管理员!");
			} else {
				StringBuilder sb = new StringBuilder();
				try (BufferedReader reader = req.getReader();) {
					char[] buff = new char[1024];
					int len;
					while ((len = reader.read(buff)) != -1) {
						sb.append(buff, 0, len);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				JSONObject jsonObject = JSONObject.fromObject(sb.toString());
				JSONArray jsonarr = JSONArray.fromObject(jsonObject.getString("seids"));

				String status="fail";
				String ms="删除失败!";
				for (int i = 0; i < jsonarr.size(); i++) {
					int id = Integer.valueOf(jsonarr.get(i).toString());
					SecondarySector sectors = secondarysectorservice.load(id);
					
					if(null!=sectors){
						int rows = sectorprojectsservice.projectrowfordmtp(id);
						if(rows>=1){
							status="fail";
							ms=sectors.getDepartmentname()+"有相关联的项目信息，不能删除!";
							break;
						}
					}
					
					secondarysectorservice.delete(sectors);
					operationlogservice.add(req, "SectorProjects", id, 
							99,"部门删除成功！部门名："+sectors.getDepartmentname());
					status="success";
					ms="删除部门成功!";
				}
				json.put("status", status);
				json.put("ms", ms);
			}
			pw.print(json.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
}
