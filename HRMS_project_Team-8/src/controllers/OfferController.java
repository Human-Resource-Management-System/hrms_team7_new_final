package controllers;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import DAO_Interfaces.CandidateDAO;
import models.Candidate;
import models.EmploymentOfferDocument;
import models.EmploymentOfferdocComposite;
import models.HRDepartment;
import models.HrmsEmploymentOffer;
import models.InductionDocumentTypes;
import models.OfferModel;
import service.offerlettermail;

@Controller
public class OfferController {
	private CandidateDAO cd;
	OfferModel of;
	Candidate can;
	InductionDocumentTypes indoc;

	@Autowired
	public OfferController(CandidateDAO cd) {
		this.cd = cd;
	}

	@RequestMapping("/provided")
	// getting data of candidates whose offerletters are already provided
	public String getofferletterprovidedcandidates(Model model) {
		List<Candidate> candidates = cd.findAllProvidedCandidates();
		model.addAttribute("candidates", candidates);
		return "offerlettercandidates";
	}

	// getting data of candidates whose offerletters have to be issue
	@RequestMapping("/issue")
	public String getissuingCandidates(Model model) {
		List<Candidate> candidates = cd.findAllIssuedCandidates();
		model.addAttribute("candidates", candidates);
		return "offerlettercandidates";

	}

	@RequestMapping("/get-candidate-details")
	public String getCandidateDetails(@RequestParam("id") int candidateId, Model model, HttpSession session) {
		Candidate candidate = cd.getCandidateById(candidateId);
		session.setAttribute("candidate", candidate);

		// set admin session variable
		int id = (int) session.getAttribute("adminId");
		HRDepartment emp = cd.getHrById(id);
		System.out.println("mobileno" + emp.getMobileNumber());
		List<String> listOfDocuments = cd.getAllDocuments();
		model.addAttribute("candidate", candidate);
		System.out.println(candidate);
		model.addAttribute("hr", emp);
		model.addAttribute("listOfDocuments", listOfDocuments);

		return "viewCandidatess";
	}

	@RequestMapping("/email")
	public String sendToMail(@Validated OfferModel offerModel, Model model) {
		of = offerModel;

		System.out.println(offerModel.getDocuments());
		model.addAttribute("offerModel", offerModel);

		// Return the appropriate view
		return "email";
	}

	public String redirectedFromOfferLetter(HrmsEmploymentOffer eofr, HttpSession session,
			EmploymentOfferdocComposite employmentofferdocComposite, EmploymentOfferDocument employmentofferdocument,
			Model model, HttpServletRequest request, HttpServletResponse response) {

		eofr.setOfferId((int) (cd.getLatestEofrIdFromDatabase() + 1));
		eofr.setReferenceId("ref " + eofr.getOfferId());
		eofr.setCandidateId(Integer.parseInt(of.getCandidateId()));
		System.out.println(of);

		eofr.setHrEmail(of.getAdminEmail());
		eofr.setHrMobileNumber(Long.parseLong(of.getAdminMobile()));
		eofr.setOfferDate(Date.valueOf(of.getOfferDate()));
		eofr.setOfferedJob(of.getOfferedJob());
		eofr.setReportingDate(Date.valueOf(LocalDate.parse(of.getReportingDate())));
		eofr.setStatus("INPR");

		try {
			offerlettermail.sendEmail(request, response, of);
		} catch (Exception e) {

			e.printStackTrace();
		}
		Candidate candidate = (Candidate) session.getAttribute("candidate");
		System.out.println(candidate + "print sesssion candidate");
		cd.updateCandidateStatus(candidate, "cand_status", "AC");

		// cd.insertEofrInto(eofr);

		// cd.updateEmploymentOfferDocuments(eofr, of, employmentofferdocComposite, employmentofferdocument);

		return "front";
	}

}