package eionet.rod.util;

import eionet.rod.service.FileServiceIF;
import eionet.rod.service.modules.FileServiceImpl;
import eionet.rod.util.exception.ServiceException;

public class RODServices {

    private static FileServiceIF _fSrv = null;

    /**
     * Instance of FileServiceIF (reads from props file).
     *
     * @return FileServiceIF
     * @throws ServiceException
     */
    public static FileServiceIF getFileService() throws ServiceException {
        if (_fSrv == null) {
            _fSrv = new FileServiceImpl();
        }
        return _fSrv;
    }

}
