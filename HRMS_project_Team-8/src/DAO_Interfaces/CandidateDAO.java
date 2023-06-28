package DAO_Interfaces;

import java.util.List;

import models.Candidate;
import models.EmploymentOfferDocument;
import models.EmploymentOfferdocComposite;
import models.HRDepartment;
import models.HrmsEmploymentOffer;
import models.OfferModel;

public interface CandidateDAO {

	/**
	 * Retrieves a list of all candidates.
	 *
	 * @return A list of all candidates.
	 */
	List<Candidate> getAllCandidates();

	/**
	 * Saves a candidate.
	 *
	 * @param candidate The candidate to be saved.
	 */
	void saveCandidate(Candidate candidate);

	/**
	 * Retrieves a candidate by their ID.
	 *
	 * @param candidateId The ID of the candidate.
	 * @return The candidate with the specified ID.
	 */
	Candidate getCandidateById(int candidateId);

	/**
	 * Retrieves a list of all candidates whose offer letter has to be issued.
	 *
	 * @return A list of candidates whose offer letter has to be issued.
	 */
	List<Candidate> findAllIssuedCandidates();

	/**
	 * Retrieves the latest HrmsEmploymentOffer ID from the database.
	 *
	 * @return The latest HrmsEmploymentOffer ID from the database.
	 */
	int getLatestEofrIdFromDatabase();

	/**
	 * Inserts an HrmsEmploymentOffer (Employment Offer) into the database.
	 *
	 * @param eofr The HrmsEmploymentOffer to be inserted.
	 */
	void insertEofrInto(HrmsEmploymentOffer eofr);

	/**
	 * Retrieves an HR Department by its ID.
	 *
	 * @param hR_id The ID of the HR Department.
	 * @return The HR Department with the specified ID.
	 */
	HRDepartment getHrById(int hR_id);

	/**
	 * Retrieves a list of all available documents.
	 *
	 * @return A list of all available documents.
	 */
	List<String> getAllDocuments();

	/**
	 * Updates the employment offer documents for an HrmsEmploymentOffer (Employment Offer).
	 *
	 * @param eofr       The HrmsEmploymentOffer to be updated.
	 * @param offerModel The OfferModel containing the updated employment offer documents.
	 */
	void updateEmploymentOfferDocuments(HrmsEmploymentOffer employmentOfferModel, OfferModel of,
			EmploymentOfferdocComposite empoffdocComposite, EmploymentOfferDocument employmentofferdocument);

	/**
	 * Updates the status of a candidate.
	 * 
	 * @param candidate
	 *
	 * @param cand_status The current status of the candidate.
	 * @param newValue    The new status value to be set.
	 */

	void updateCandidateStatus(Candidate candidate, String cand_status, String newValue);

	/**
	 * Retrieves a list of all candidates whose offer letter has been issued.
	 *
	 * @return A list of candidates whose offer letter has been issued.
	 */
	public List<Candidate> findAllProvidedCandidates();

}
