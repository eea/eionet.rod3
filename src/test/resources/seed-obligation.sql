DELETE FROM T_OBLIGATION;
INSERT INTO T_OBLIGATION  SET
        PK_RA_ID =1,
        FK_SOURCE_ID=1,
        VALID_SINCE='2018-07-15' ,
        TITLE='Fuel Quality Directive Article 7a',
        FORMAT_NAME='Transmission of Information questionnaire',
        REPORT_FORMAT_URL='Council Directive (EU) 2015/652 of 20 April 2015 laying down calculation methods and reporting requi',
        REPORT_FREQ_DETAIL='31 September',
        REPORTING_FORMAT='Information shall be supplied according to Article 17 of the Directive and Commission Decision 98/184/EC of 25 February 1998 concerning a questionnaire for Member States'' reports on the implementation of Council Directive 94/67/EC on the incineration of hazardous waste (implementation of Council Directive 91/692/EEC)',
        DATE_COMMENTS='For Member States that acceeded to the EU on 1 January 2007 (Bulgaria, Romania) the reporting concerning the period 2004-2006 is voluntary.',
        LAST_UPDATE='2007-06-29',
        TERMINATE='N',
        REPORT_FREQ='annually',
        COMMENT='Reporting Obligation of Council Directive 94/67/EC on the incineration of hazardous waste will be repealed after 28/12/2005 by Directive 200/76/EC on the incineration of waste. The last report shall cover the three year period 2004-2006.',
        RESPONSIBLE_ROLE='eionet-nrc-waterquality',
        NEXT_DEADLINE='2013-10-31',
        FIRST_REPORTING='2005-10-31',
        REPORT_FREQ_MONTHS='12',
        NEXT_REPORTING='From bathing season 2007 onwards, MS can choose to report either under Directive 76/160/EEC or Directive 2006/7/EC. Reporting under Directive 2006/7/EC becomes obligatory from the 2012 bathing season on. Visit http://rod.eionet.europa.eu/instruments/609',
        VALID_TO='2014-12-30',
        FK_DELIVERY_COUNTRY_IDS=',34,36,28,',
        NEXT_DEADLINE2='2022-09-30',
        LAST_HARVESTED='2017-11-06 10:14:35',
        LOCATION_PTR='http://cdr.eionet.europa.eu',
        LOCATION_INFO='CDR',
        DESCRIPTION='Water quality data in transitional, coastal and marine waters. Includes data on nutrients, organic matter, chlorophyll-a, hazardous substances and general physico-chemical parameters in water, sediment and biota.',
        RESPONSIBLE_ROLE_SUF=1,
        NATIONAL_CONTACT='Authorised WISE data providers for BWDs',
        NATIONAL_CONTACT_URL='http://forum.eionet.europa.eu/x_wise-reporting/library/bathing_directive/reporting_bathing_2011/list-bathing-water-directive-reporters_20120529',
        COORDINATOR_ROLE='eionet-nfp',
        COORDINATOR_ROLE_SUF=0,
        COORDINATOR='Country contacts',
        COORDINATOR_URL='http://www.pops.int/documents/implementation/nips/NIPsContactTable.pdf',
        EEA_PRIMARY=0,
        EEA_CORE=0,
        FLAGGED=0,
        AUTHORITY='Article 15',
        DATA_USED_FOR='http://www.pops.int/documents/implementation/nips/submissions/default.htm',
        DATA_USED_FOR_URL='http://www.pops.int/documents/implementation/nips/submissions/default.htm',
        CONTINOUS_REPORTING='no';

INSERT INTO T_OBLIGATION  SET
    PK_RA_ID =2,
    FK_SOURCE_ID=15,
    VALID_SINCE='2018-06-12' ,
    TITLE='Test 2 - Fuel Quality Directive Article 7a',
    FORMAT_NAME='Test 2 - Transmission of Information questionnaire',
    REPORT_FORMAT_URL='Test 2 - Council Directive (EU) 2015/652 of 20 April 2015 laying down calculation methods and reporting requi',
    REPORT_FREQ_DETAIL='31 September',
    REPORTING_FORMAT='Test 2 - Information shall be supplied according to Article 17 of the Directive and Commission Decision 98/184/EC of 25 February 1998 concerning a questionnaire for Member States'' reports on the implementation of Council Directive 94/67/EC on the incineration of hazardous waste (implementation of Council Directive 91/692/EEC)',
    DATE_COMMENTS='Test 2 - For Member States that acceeded to the EU on 1 January 2007 (Bulgaria, Romania) the reporting concerning the period 2004-2006 is voluntary.',
    LAST_UPDATE='2007-06-29',
    TERMINATE='Y',
    REPORT_FREQ='annually',
    COMMENT='Test 2 - Reporting Obligation of Council Directive 94/67/EC on the incineration of hazardous waste will be repealed after 28/12/2005 by Directive 200/76/EC on the incineration of waste. The last report shall cover the three year period 2004-2006.',
    RESPONSIBLE_ROLE='eionet-nrc-waterquality',
    NEXT_DEADLINE='2013-10-31',
    FIRST_REPORTING='2005-10-31',
    REPORT_FREQ_MONTHS='12',
    NEXT_REPORTING='From bathing season 2007 onwards, MS can choose to report either under Directive 76/160/EEC or Directive 2006/7/EC. Reporting under Directive 2006/7/EC becomes obligatory from the 2012 bathing season on. Visit http://rod.eionet.europa.eu/instruments/609',
    VALID_TO='2014-12-30',
    FK_DELIVERY_COUNTRY_IDS=',34,36,28,',
    NEXT_DEADLINE2='2022-09-30',
    LAST_HARVESTED='2017-11-06 10:14:35',
    LOCATION_PTR='http://cdr.eionet.europa.eu',
    LOCATION_INFO='CDR',
    DESCRIPTION='Water quality data in transitional, coastal and marine waters. Includes data on nutrients, organic matter, chlorophyll-a, hazardous substances and general physico-chemical parameters in water, sediment and biota.',
    RESPONSIBLE_ROLE_SUF='1',
    NATIONAL_CONTACT='Authorised WISE data providers for BWDs',
    NATIONAL_CONTACT_URL='http://forum.eionet.europa.eu/x_wise-reporting/library/bathing_directive/reporting_bathing_2011/list-bathing-water-directive-reporters_20120529',
    COORDINATOR_ROLE='eionet-nfp',
    COORDINATOR_ROLE_SUF='0',
    COORDINATOR='Country contacts',
    COORDINATOR_URL='http://www.pops.int/documents/implementation/nips/NIPsContactTable.pdf',
    EEA_PRIMARY='0',
    EEA_CORE='0',
    FLAGGED='0',
    AUTHORITY='Article 15',
    DATA_USED_FOR='http://www.pops.int/documents/implementation/nips/submissions/default.htm',
    DATA_USED_FOR_URL='http://www.pops.int/documents/implementation/nips/submissions/default.htm',
    CONTINOUS_REPORTING='no';

DELETE FROM T_ISSUE;
INSERT INTO T_ISSUE SET PK_ISSUE_ID=1, ISSUE_NAME='Climate Change';
INSERT INTO T_ISSUE SET PK_ISSUE_ID=2, ISSUE_NAME='Ozone Depletion';

DELETE FROM T_RAISSUE_LNK;
INSERT INTO T_RAISSUE_LNK VALUES (1,1),(1,2);

DELETE FROM T_SPATIAL;
INSERT INTO T_SPATIAL (PK_SPATIAL_ID,SPATIAL_NAME, SPATIAL_TYPE, SPATIAL_TWOLETTER, SPATIAL_ISMEMBERCOUNTRY)
 VALUES (1, 'Austria','C','AT','Y'),
        (2,'Albania','C','AL','N'),
        (3,'Francia','C','FR','Y');

DELETE FROM T_RASPATIAL_LNK;
INSERT INTO T_RASPATIAL_LNK VALUES (1,1,'N'),(1,2,'Y');

DELETE FROM T_ROLE;
INSERT INTO T_ROLE (ROLE_NAME,ROLE_ID) VALUES ('National Focal Points','eionet-nrc-waterquality');

DELETE FROM T_CLIENT;
INSERT INTO T_CLIENT (PK_CLIENT_ID, CLIENT_NAME, CLIENT_ACRONYM,
    CLIENT_URL, CLIENT_ADDRESS, CLIENT_EMAIL, DESCRIPTION, POSTAL_CODE,
    CITY, COUNTRY, CLIENT_SHORT_NAME) VALUES (
    1,'Test client','TC', 'http://www.unep.org/ozone', 'Nowhere', 'nowhere@example.com'
    , 'description', '0000', 'Longyearbyen', 'Norway', 'Test');
INSERT INTO T_CLIENT (PK_CLIENT_ID, CLIENT_NAME, CLIENT_ACRONYM,
    CLIENT_URL, CLIENT_ADDRESS, CLIENT_EMAIL, DESCRIPTION, POSTAL_CODE,
    CITY, COUNTRY, CLIENT_SHORT_NAME) VALUES (2, 'Test client2', 'TC2',
    'http://www.unep.org/ozone', 'Nowhere', 'nowhere@example.com',
    'description2', '0000', 'Viena', 'Austria', 'Test2');

DELETE FROM T_CLIENT_OBLIGATION_LNK;
INSERT INTO T_CLIENT_OBLIGATION_LNK VALUES (1,2,'C'),(1,1,'M'),(1,1,'C');
