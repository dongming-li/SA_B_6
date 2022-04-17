package nerdsbattle.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DocController {

  @RequestMapping("/docs")
  public RedirectView showDocs() {
    RedirectView redirectView = new RedirectView();
    redirectView.setUrl("http://proj-309-sa-b-6.cs.iastate.edu/docs");
    return redirectView;
  }
}
