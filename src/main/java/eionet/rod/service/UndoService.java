package eionet.rod.service;

import eionet.rod.model.UndoDTO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.util.List;

public interface UndoService {

    void insertIntoUndo(long ts, String table, String column, String state, String quotes, String isPrimary, String value, int rowCnt, String show);

    void insertIntoUndo(Integer id, String state, String table, String idField, long ts, String extraSQL, String show);

    void insertTransactionInfo(Integer id, String state, String table, String idField, long ts, String extraSQL);

    void addObligationIdsIntoUndo(Integer id, long ts, String table);

    List<UndoDTO> getPreviousActionsReportSpecific(Integer id, String tab, String idField, String operation);

    List<UndoDTO> getUndoList(long ts, String table, String op);

    List<UndoDTO> getPreviousActionsGeneral();

    boolean isDelete(String table, String column, Integer id);

    List<UndoDTO> getUpdateHistory(String extraSQL, MapSqlParameterSource p);

    List<UndoDTO> getUndoInformation(long ts, String op, String tab);
}
