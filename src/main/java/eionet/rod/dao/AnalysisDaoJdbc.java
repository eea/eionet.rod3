package eionet.rod.dao;

import eionet.rod.model.AnalysisDTO;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Date;


public class AnalysisDaoJdbc implements AnalysisDao {

    private DataSource dataSource;

    JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public AnalysisDTO getStatistics() {

        Number number;
        Date date;

        AnalysisDTO analysisDTORec = new AnalysisDTO();

        String query = "SELECT COUNT(*) AS totalReportingObligation FROM T_OBLIGATION";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer totalReportingObligation = number.intValue();
        analysisDTORec.setTotalReportingObligation(totalReportingObligation);

        query = "SELECT COUNT(*) AS totalReportingObligation FROM T_OBLIGATION WHERE TERMINATE = 'N'";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer openedReportingObligation = number.intValue();
        analysisDTORec.setOpenedReportingObligation(openedReportingObligation);

        query = "SELECT COUNT(*) AS totalReportingObligation FROM T_OBLIGATION WHERE TERMINATE = 'Y'";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer terminatedReportingObligation = number.intValue();
        analysisDTORec.setTerminatedReportingObligation(terminatedReportingObligation);

        query = "SELECT MAX(LAST_UPDATE) AS lastUpdateReportingObligation FROM T_OBLIGATION";
        date = jdbcTemplate.queryForObject(query, Date.class);
        analysisDTORec.setLastUpdateReportingObligation(date);

        query = "SELECT COUNT(*) AS totalLegalInstrument FROM T_SOURCE";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer totalLegalInstrument = number.intValue();
        analysisDTORec.setTotalLegalInstrument(totalLegalInstrument);

        query = "SELECT MAX(LAST_UPDATE) AS lastUpdateLegalInstrument FROM T_SOURCE";
        date = jdbcTemplate.queryForObject(query, Date.class);
        analysisDTORec.setLastUpdateLegalInstrument(date);

        query = "SELECT COUNT(PK_RA_ID) AS eeaCore FROM T_OBLIGATION WHERE EEA_CORE=1";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer eeaCore = number.intValue();
        analysisDTORec.setEeaCore(eeaCore);

        query = "SELECT COUNT(PK_RA_ID) AS eeaPriority FROM T_OBLIGATION WHERE EEA_PRIMARY=1";
        number = jdbcTemplate.queryForObject(query, Integer.class);
        Integer eeaPriority = number.intValue();
        analysisDTORec.setEeaPriority(eeaPriority);

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
