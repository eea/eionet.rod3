package eionet.rod.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import eionet.rod.dao.HelpDao;
import eionet.rod.model.Help;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;

@Controller
@RequestMapping("/help.do")
public class HelpController {

	@Autowired
	private HelpDao helpDao;
	     
	@RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Help helpLoadData(@RequestParam("helpId") String helpId) {
        if (helpId != null) {
        	Help HelpData = helpDao.findId(helpId);
        	String text = RODUtil.replaceTags(HelpData.getText());
        	HelpData.setText(text);
        	
        	return HelpData;
        }else {
        	throw new ResourceNotFoundException("No data to param helpId");
        }
       
    }
}
