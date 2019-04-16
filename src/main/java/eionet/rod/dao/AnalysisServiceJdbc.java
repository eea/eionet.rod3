package eionet.rod.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.AnalysisDTO;

@Service(value = "analysisService")
@Transactional
public class AnalysisServiceJdbc implements AnalysisService {

	private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
	
	@Override
	public AnalysisDTO getStatistics() {
		
		Number number;
		String date;
		
		AnalysisDTO analysisDTORec = new AnalysisDTO();
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		String query = "SELECT COUNT(*) AS totalRa FROM T_OBLIGATION";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer totalRa = number.intValue();
		query = "SELECT MAX(LAST_UPDATE) AS lastUpdateRa FROM T_OBLIGATION";
		date = jdbcTemplate.queryForObject(query, String.class);
		analysisDTORec.setTotalRa(totalRa);
		analysisDTORec.setLastUpdateRa(date.substring(0, 10));
		
		query = "SELECT COUNT(*) AS totalLi FROM T_SOURCE";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer totalLi = number.intValue();
		query = "SELECT MAX(LAST_UPDATE) AS lastUpdateLi FROM T_SOURCE";
		date = jdbcTemplate.queryForObject(query, String.class);
		analysisDTORec.setTotalLi(totalLi);
		analysisDTORec.setLastUpdateLi(date.substring(0, 10));
		
		query = "SELECT COUNT(PK_RA_ID) AS eeaCore FROM T_OBLIGATION WHERE EEA_CORE=1";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer eeaCore = number.intValue();
		analysisDTORec.setEeaCore(eeaCore);
		
		query = "SELECT COUNT(PK_RA_ID) AS eeaPriority FROM T_OBLIGATION WHERE EEA_PRIMARY=1";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer eeaPriority = number.intValue();
		analysisDTORec.setEeaPriority(eeaPriority);
		
//		query = "SELECT COUNT(PK_RA_ID) AS overlapRa FROM T_OBLIGATION WHERE OVERLAP_URL IS NOT NULL AND OVERLAP_URL != ''";
//		number = jdbcTemplate.queryForObject(query, Integer.class);
//		Integer overlapRa = number.intValue();
//		analysisDTORec.setOverlapRa(overlapRa);
		
		query = "SELECT COUNT(PK_RA_ID) AS flaggedRa FROM T_OBLIGATION WHERE FLAGGED=1";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer flaggedRa = number.intValue();
		analysisDTORec.setFlaggedRa(flaggedRa);
		
		query = "SELECT COUNT(PK_RA_ID) as noIssue FROM T_OBLIGATION WHERE TERMINATE='N' AND PK_RA_ID NOT IN (SELECT DISTINCT FK_RA_ID FROM T_RAISSUE_LNK)";
		number = jdbcTemplate.queryForObject(query, Integer.class);
		Integer noIssue = number.intValue();
		analysisDTORec.setNoIssue(noIssue);
		
		return analysisDTORec;
	}

}
