package DAO;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import DAO_Interfaces.CandidateDAO;
import models.Candidate;
import models.EmploymentOfferDocument;
import models.EmploymentOfferdocComposite;
import models.HRDepartment;
import models.HrmsEmploymentOffer;
import models.InductionDocumentTypes;
import models.OfferModel;

@Repository
public class CandidateDAOImpl implements CandidateDAO {

	@PersistenceContext
	private EntityManager entityManager;
	int eofrI;

	@Override
	@Transactional
	public void saveCandidate(Candidate candidate) {
		entityManager.persist(candidate);
	}

	// get candidate details by ID
	@Override
	public Candidate getCandidateById(int candidateId) {
		return entityManager.find(Candidate.class, candidateId);

	}

	@Override
	@Transactional
	public List<Candidate> getAllCandidates() {
		String query = "SELECT c FROM Candidate c";
		return entityManager.createQuery(query, Candidate.class).getResultList();
	}

	// get all the candidates list for whom the offer letter have to be send
	@Override
	public List<Candidate> findAllIssuedCandidates() {
		TypedQuery<Candidate> query = entityManager
				.createQuery("SELECT c FROM Candidate c WHERE c.candStatus = :status", Candidate.class);
		query.setParameter("status", "NA");
		return query.getResultList();
	}

	// update the candidate status from NA to AC after issuing offer letters.
	@Override
	@Transactional
	public void updateCandidateStatus(Candidate cann, String cand_status, String newValue) {
		cann.setCandStatus(newValue); // Modify the desired column value
		entityManager.merge(cann); // Save the changes to the database
	}

	// insert the new candidate information in employment offers table
	@Override
	@Transactional
	public void insertEofrInto(HrmsEmploymentOffer eofr) {
		entityManager.persist(eofr);
	}

	// getting latest employment offerId for incrementing the eofrId for new row
	@Override
	public int getLatestEofrIdFromDatabase() {
		TypedQuery<Integer> query = entityManager
				.createQuery("SELECT CAST(MAX(e.offerId) AS int) FROM HrmsEmploymentOffer e", Integer.class);
		return query.getSingleResult();
	}

	// get all the induction documents to select by HR , the documents should bring by candidate while coming to
	// induction program
	@Override
	public List<String> getAllDocuments() {
		TypedQuery<String> query = entityManager.createQuery("SELECT e.idtyTitle FROM InductionDocumentTypes e",
				String.class);
		return query.getResultList();
	}

	// to insert offerId, docIndex,IdtyId of the particular candidate in employment offers documents

	@Override
	@Transactional
	public void updateEmploymentOfferDocuments(HrmsEmploymentOffer employmentOfferModel, OfferModel of,
			EmploymentOfferdocComposite empoffdocComposite, EmploymentOfferDocument employmentofferdocument) {

		System.out.println("in here");
		// getting eofrId
		int eofrId = employmentOfferModel.getCandidateId();
		// getting the list of documents should bring by candidate
		List<String> documentsToBring = of.getDocuments();
		System.out.println(documentsToBring);
		// setting inductionDocumentTypes model from inductionDocumentTypes table
		List<InductionDocumentTypes> inductionDocuments = getInductionDocuments();

		System.out.println(inductionDocuments);
		int docIndex = 1;
		for (String document : documentsToBring) {
			// getting IdtyId by the document name
			int idtyId = findIdtyIdByTitle(inductionDocuments, document);
			// these four steps is for assigning eofrId,docIndex,idtyId to the employmentofferdocuments entity model
			empoffdocComposite.setOfferid(eofrId);
			empoffdocComposite.setDocumentIndex(docIndex);
			employmentofferdocument.setEmpoff(empoffdocComposite);
			employmentofferdocument.setOfferidentity(idtyId);
			// EmploymentOfferDocument documentModel = new EmploymentOfferDocument(empoffdocComposite, idtyId);

			System.out.println(employmentofferdocument);
			// update the data into data base which got from entity model of employmentofferdocuments
			saveEmploymentOfferDocument(employmentofferdocument);
			docIndex++;
		}
	}

	private List<InductionDocumentTypes> getInductionDocuments() {
		TypedQuery<InductionDocumentTypes> query = entityManager.createQuery("SELECT d FROM InductionDocumentTypes d",
				InductionDocumentTypes.class);
		return query.getResultList();
	}

	// persists into employmenofferdocuemnts table
	@Transactional
	private void saveEmploymentOfferDocument(EmploymentOfferDocument document) {
		entityManager.persist(document);
	}

	// getting IdtyId by the document name
	private int findIdtyIdByTitle(List<InductionDocumentTypes> inductionDocuments, String title) {
		for (InductionDocumentTypes document : inductionDocuments) {
			if (document.getTitle().equalsIgnoreCase(title)) {
				return document.getDocumentTypeId();
			}
		}
		return 0; // Return an appropriate default value if the document title is not found
	}

	@Override
	public HRDepartment getHrById(int hR_id) {
		String queryString = "SELECT hr FROM HRDepartment hr WHERE hr.employeeId = :id";
		TypedQuery<HRDepartment> query = entityManager.createQuery(queryString, HRDepartment.class);
		query.setParameter("id", hR_id);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null; // Return null if no HR department is found for the given ID
		}
	}

	@Override
	public List<Candidate> findAllProvidedCandidates() {
		TypedQuery<Candidate> query = entityManager
				.createQuery("SELECT c FROM Candidate c WHERE c.candStatus = :status", Candidate.class);
		query.setParameter("status", "AC");
		return query.getResultList();
	}

}
