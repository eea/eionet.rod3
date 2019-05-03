package eionet.rod.controller;

import eionet.rod.dao.HelpDao;
import eionet.rod.model.Help;
import eionet.rod.util.RODUtil;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/help.do")
public class HelpController {

    @Autowired
    private HelpDao helpDao;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public Help helpLoadData(@RequestParam("helpId") String helpId) {
        if (helpId != null) {
            Help helpData = helpDao.findId(helpId);
            String text = RODUtil.replaceTags(helpData.getText());
            helpData.setText(text);

            return helpData;
        } else {
            throw new ResourceNotFoundException("No data to param helpId");
        }

    }
}
