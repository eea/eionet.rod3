package eionet.rod.service;

import eionet.rod.dao.SourceDao;
import eionet.rod.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "sourceService")
@Transactional
public class SourceServiceImpl implements SourceService {

    @Autowired
    SourceDao sourceDao;

    @Override
    public InstrumentFactsheetDTO getById(Integer sourceId) {
        return sourceDao.getById(sourceId);
    }

    @Override
    public List<InstrumentObligationDTO> getObligationsById(Integer sourceId) {
        return sourceDao.getObligationsById(sourceId);
    }

    @Override
    public void update(InstrumentFactsheetDTO instrumentFactsheetRec) {
        sourceDao.update(instrumentFactsheetRec);
    }

    @Override
    public Integer insert(InstrumentFactsheetDTO instrumentFactsheetRec) {
        return sourceDao.insert(instrumentFactsheetRec);
    }

    @Override
    public List<InstrumentFactsheetDTO> getAllInstruments() {
        return sourceDao.getAllInstruments();
    }

    @Override
    public List<InstrumentClassificationDTO> getAllClassifications() {
        return sourceDao.getAllClassifications();
    }

    @Override
    public void insertClassifications(InstrumentFactsheetDTO instrumentFactsheetRec) {
        sourceDao.insertClassifications(instrumentFactsheetRec);
    }

    @Override
    public void deleteClassifications(Integer sourceId) {
        sourceDao.deleteClassifications(sourceId);
    }

    @Override
    public String getHierarchy(Integer id, boolean hasParent, String mode) {
        return sourceDao.getHierarchy(id, hasParent, mode);
    }

    @Override
    public InstrumentsListDTO getHierarchyInstrument(Integer id) {
        return sourceDao.getHierarchyInstrument(id);
    }

    @Override
    public List<HierarchyInstrumentDTO> getHierarchyInstruments(Integer id) {
        return sourceDao.getHierarchyInstruments(id);
    }

    @Override
    public void delete(Integer sourceId) {
        sourceDao.delete(sourceId);
    }
}
