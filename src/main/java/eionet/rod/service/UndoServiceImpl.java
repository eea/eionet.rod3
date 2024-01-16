package eionet.rod.service;

import eionet.rod.dao.UndoDao;
import eionet.rod.model.UndoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "undoService")
@Transactional
public class UndoServiceImpl implements UndoService {

    @Autowired
    UndoDao undoDao;

    @Override
    public void insertIntoUndo(long ts, String table, String column, String state, String quotes, String isPrimary, String value, int rowCnt, String show) {
        undoDao.insertIntoUndo(ts, table, column, state, quotes, isPrimary, value, rowCnt, show);
    }

    @Override
    public void insertIntoUndo(Integer id, String state, String table, String idField, long ts, String extraSQL, String show) {
        undoDao.insertIntoUndo(id, state, table, idField, ts, extraSQL, show);
    }

    @Override
    public void insertTransactionInfo(Integer id, String state, String table, String idField, long ts, String extraSQL) {
        undoDao.insertTransactionInfo(id, state, table, idField, ts, extraSQL);
    }

    @Override
    public void addObligationIdsIntoUndo(Integer id, long ts, String table) {
        undoDao.addObligationIdsIntoUndo(id, ts, table);
    }

    @Override
    public List<UndoDTO> getPreviousActionsReportSpecific(Integer id, String tab, String idField, String operation) {
        return undoDao.getPreviousActionsReportSpecific(id, tab, idField, operation);
    }

    @Override
    public List<UndoDTO> getUndoList(long ts, String table, String op) {
        return undoDao.getUndoList(ts, table, op);
    }

    @Override
    public List<UndoDTO> getPreviousActionsGeneral() {
        return undoDao.getPreviousActionsGeneral();
    }

    @Override
    public boolean isDelete(String table, String column, Integer id) {
        return undoDao.isDelete(table, column, id);
    }

    @Override
    public List<UndoDTO> getUpdateHistory(String extraSQL, MapSqlParameterSource parameterSource) {
        return undoDao.getUpdateHistory(extraSQL, parameterSource);
    }

    @Override
    public List<UndoDTO> getUndoInformation(long ts, String op, String tab) {
        return undoDao.getUndoInformation(ts, op, tab);
    }
}
