package eionet.rod.dao;

import eionet.rod.model.UndoDTO;

public interface UndoService {

	void insert(UndoDTO undoRec);
	
	void deleteByPK(Integer undoTime, String table, String column, String operation, Integer subTransNr);
	
	void update(UndoDTO undoRec);
	
}
