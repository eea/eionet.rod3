package eionet.rod.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import eionet.rod.model.UndoDTO;

@Service
public class UndoServiceJdbc implements UndoService {
	
	private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

	@Override
	public void insert(UndoDTO undoRec) {
		String query = "INSERT INTO T_UNDO (UNDO_TIME, TAB, "
                + "COL, OPERATION, QUOTES, P_KEY, VALUE, "
                + "SUB_TRANS_NR, SHOW_OBJECT) VALUES (?,?,?,?,?,?,?,?,?)";
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(query,
				undoRec.getUndoTime(),
				undoRec.getTable(),
				undoRec.getColumn(),
				undoRec.getOperation(),
				undoRec.getQuotes(),
				undoRec.getPrimaryKey(),
				undoRec.getValue(),
				undoRec.getSubTransNr(),
				undoRec.getShow()
				);
	}

	@Override
	public void deleteByPK(Integer undoTime, String table, String column, String operation, Integer subTransNr) {
		String query = "DELETE FROM T_UNDO WHERE UNDO_TIME = ? AND TAB = ? AND "
				+ "COL = ? AND OPERATION = ? AND SUB_TRANS_NR = ?";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(query, undoTime, table, column, operation, subTransNr);
	}

	@Override
	public void update(UndoDTO undoRec) {
		String update = "UPDATE T_UNDO SET QUOTES = ?, P_KEY = ?, "
                + "VALUE = ?, SHOW_OBJECT = ? "
                + "WHERE UNDO_TIME = ? AND TAB = ? AND "
                + "COL = ? AND OPERATION = ? AND SUB_TRANS_NR = ?";
		 JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
	        jdbcTemplate.update(update,
	        		undoRec.getQuotes(),
	        		undoRec.getPrimaryKey(),
	        		undoRec.getValue(),
	        		undoRec.getShow(),
	        		undoRec.getUndoTime(),
	        		undoRec.getTable(),
	        		undoRec.getColumn(),
	        		undoRec.getOperation(),
	        		undoRec.getSubTransNr()
	        		);
	}

}
