package christopher.model;

public class StationData {
    String id;
    String date;
    String element;
    String data;
    String mFlag;
    String qFlag;
    String sFlag;
    String obsTime;
    public StationData() {
    }

    /**
     * Converts a csv record into Station Data.
     * <p>From data's readme-by-year:
     * <p>The following information serves as a definition of each field in one line of data covering one station-day.
     * Each field described below is separated by a comma ( , ) and follows the order below:
     *
     * <ol>
     *     <li>ID = 11 character station identification code
     *     <li>YEAR/MONTH/DAY = 8 character date in YYYYMMDD format (e.g. 19860529 = May 29, 1986)
     *     <li>ELEMENT = 4 character indicator of element type
     *     <li>DATA VALUE = 5 character data value for ELEMENT
     *     <li>M-FLAG = 1 character Measurement Flag
     *     <li>Q-FLAG = 1 character Quality Flag
     *     <li>S-FLAG = 1 character Source Flag
     *     <li>OBS-TIME = 4-character time of observation in hour-minute format (i.e. 0700 =7:00 am)</li>
     * </ol>
     * @param csvRecord
     */
    public StationData(String csvRecord) {
        String[] csvRecordArray = csvRecord.split(",", 8);

        id = csvRecordArray[0];
        date = csvRecordArray[1];
        element = csvRecordArray[2];
        data = csvRecordArray[3];
        mFlag = csvRecordArray[4];
        qFlag = csvRecordArray[5];
        sFlag = csvRecordArray[6];
        obsTime = csvRecordArray[7];
    }

    public StationData(String id, String date, String element, String data, String mFlag, String qFlag,
                       String sFlag, String obsTime) {
        this.id = id;
        this.date = date;
        this.element = element;
        this.data = data;
        this.mFlag = mFlag;
        this.qFlag = qFlag;
        this.sFlag = sFlag;
        this.obsTime = obsTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObsTime() {
        return obsTime;
    }

    public void setObsTime(String obsTime) {
        this.obsTime = obsTime;
    }

    public String getmFlag() {
        return mFlag;
    }

    public void setmFlag(String mFlag) {
        this.mFlag = mFlag;
    }

    public String getqFlag() {
        return qFlag;
    }

    public void setqFlag(String qFlag) {
        this.qFlag = qFlag;
    }

    public String getsFlag() {
        return sFlag;
    }

    public void setsFlag(String sFlag) {
        this.sFlag = sFlag;
    }
}
