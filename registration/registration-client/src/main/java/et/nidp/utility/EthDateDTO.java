package et.nidp.utility;

public class EthDateDTO {

    private int day=0;
    private  int month=0;
    private int year=0;

    public int getDay() {
        return day;
    }

    public void setDay(int date) {
        this.day = date;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public EthDateDTO()  {

    }

    @Override
    public String toString() {

        return    (day>9?day:"0"+String.valueOf(day))+  "/" + (month>9?month:"0"+String.valueOf(month))+"/"+ year ;
    }

    public EthDateDTO(int year, int month, int date)  {
            this.year = year;
            this.month = month;
            this.day = date;
    }

    public EthDateDTO(String ethiopianDate) throws Exception {
        String[] dateData=null;

        if(ethiopianDate.contains("/"))
            dateData=ethiopianDate.split("/");
        else if(ethiopianDate.contains("-"))
            dateData=ethiopianDate.split("-");
        if(dateData!=null&&dateData.length==3) {
            year = Integer.parseInt(dateData[0]);
            month = Integer.parseInt(dateData[1]);
            day = Integer.parseInt(dateData[2]);
           EthiopianDateUtility.validate(year,month,day);
        }
    }


}
