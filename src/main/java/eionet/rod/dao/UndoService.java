package eionet.rod.dao;


import java.util.List;

import eionet.rod.model.UndoDTO;

public interface UndoService {

	void insertIntoUndo(long ts, String table, String column, String state, String quotes, String isPrimary, String value, int rowCnt , String show);
	
	void insertIntoUndo(Integer id, String state, String table, String id_field, long ts, String extraSQL, String show);
	
	void insertTransactionInfo(Integer id, String state, String table, String id_field, long ts, String extraSQL);
	
	void addObligationIdsIntoUndo(Integer id, long ts, String table);
	
	List<UndoDTO> getPreviousActionsReportSpecific(Integer id, String tab, String id_field, String operation);
	
	List<UndoDTO> getUndoList(long ts, String table, String op);
	
	List<UndoDTO> getPreviousActionsGeneral();
	
	boolean isDelete(String table, String column, Integer id);
	
	List<UndoDTO> getUpdateHistory(String extraSQL);
	
}
