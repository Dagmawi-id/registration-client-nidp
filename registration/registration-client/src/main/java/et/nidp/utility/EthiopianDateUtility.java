package et.nidp.utility;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
/**
 * 1. Convert dates from Gregorian Calendar to Ethiopian Calendar and Vice-versa.
 * 2. Perform different date manipulations
 */

/**
 * @author Ethiopia National ID Pilot Project Teams
 *
 */
public class EthiopianDateUtility {


    private static int JdOffset = 1723856;

    /**This method  converts given date into Ethiopain date in yyyy/MM/dd format
     *@param date is the date from which the date is to be converted to Ethiopain date
     *@return returns string value which is Ethiopian date in yyyy/MM/dd format 
     */
    public static EthDateDTO ToEthiopianDate(Date date) {
        int jdn = toJDN(date);
        return ToEthiopianDate(jdn);
    }
	/**
	 * Julian Date Number(JDN) Convert given date to Julian number, and vice versa.
	 * @param date is the date from which the date is to be converted
	 * @return  an integer representative of the date
	 */
    private static int toJDN(Date date) {
        int a, y, m;
        a = (14 - getMonth(date))/12;
        y = getYear(date) + 4800 - a;
        m = getMonth(date) + 12 * a - 3;

        return getDate(date) + (153 * m + 2)/5 + 365 * y + y/4 - y / 100 + y /400 - 32045;
    }
	/**
	 * This method  converts given date into Ethiopain date in yyyy/MM/dd format
	 * @param jdn an integer representative of the date
	 * @return string value which is Ethiopian date in yyyy/MM/dd format 
	 */
    private static EthDateDTO ToEthiopianDate(int jdn) {
        // Formula from Dr. Berhanu Beyene and Manfred Kudlek
        int n;
        int year;
        int month;
        int day;
        int r;
        r = ((jdn - JdOffset)
                % 1461);
        n = ((r % 365) + (365
                * (r / 1460)));
        year = ((4
                * ((jdn - JdOffset)
                / 1461))
                + ((r / 365)
                - (r / 1460)));
        month = ((n / 30)
                + 1);
        day = ((n % 30)
                + 1);
        EthDateDTO ethDateDTO=new EthDateDTO();
        ethDateDTO.setDay(day);
        ethDateDTO.setMonth(month);
        ethDateDTO.setYear(year);
        //String result=String.valueOf(year)+"/"+String.valueOf(month)+"/"+String.valueOf(day);
        return ethDateDTO;//new EthiopianDate(year, month, day);
    }

    /**
     * This method  converts given date into Gregorian date format
     * @param year an integer value of the year to be converted
     * @param month an integer value of the month to be converted
     * @param day an integer value of the day to be converted
     * @return date which is converted into Gregorian date format
     * @throws Exception
     */
    public static Date ToGregorianDate(int year, int month, int day) throws Exception {
        validate(year, month, day);
        int jdn = fromEthiopianDateToJDN(year, month, day);
        return toGregorianDate(jdn);
    }

    public static Date ToGregorianDate(EthDateDTO ethDateDTO) throws Exception {
        validate(ethDateDTO.getYear(), ethDateDTO.getMonth(), ethDateDTO.getDay());
        int jdn = fromEthiopianDateToJDN(ethDateDTO.getYear(), ethDateDTO.getMonth(), ethDateDTO.getDay());
        return toGregorianDate(jdn);
    }

	/**
	 * This method  converts given date into Gregorian date format
	 * @param jdn an integer representative of the date
	 * @return date which is converted into Gregorian date format
	 */
    private static Date toGregorianDate(int jdn) {
        int day;
        int year;
        int month;
        int r = (jdn + 68569);
        int n = 4 * (r / 146097);

        r = r - (146097*n + 3)/4;
        year = 4000*(r + 1)/1461001;
        r = r - 1461*year/4 + 31;
        month = 80*r/2447;
        day = r - 2447*month/80;
        r = month/11;
        month = month + 2 - 12*r;
        year = 100*(n - 49) + year + r;

        Calendar c = Calendar.getInstance();
        c.set(year, month-1, day, 0, 0);

        return c.getTime();
    }


	/**
	 * This method validate the year, month and day values
     * @param year an integer value of the year to be converted
     * @param month an integer value of the month to be converted
     * @param day an integer value of the day to be converted
	 * @throws Exception
	 */
    public static void validate(int year, int month, int day) throws Exception {
        if (month < 1  || month > 13) {
            throw new Exception("Year, Month, and Day parameters describe an un-representable EthiopianDate.");
        }
        if(day < 1 || day > 30){
            throw new Exception("Year, Month, and Day parameters describe an un-representable EthiopianDate.");
        }

        if(month == 13  && (((year % 4)  == 3)  && (day > 6))){
            throw new Exception("Year, Month, and Day parameters describe an un-representable EthiopianDate.");
        }

        if((month == 13 && (((year % 4) != 3) && (day > 5)))){
            throw new Exception("Year, Month, and Day parameters describe an un-representable EthiopianDate.");
        }



    }
	/**
	 * This method  converts ethiopian date into Julian Date Number(JDN)
     * @param year an integer value of the year to be converted
     * @param month an integer value of the month to be converted
     * @param day an integer value of the day to be converted
	 * @return Julian Date Number(JDN)
	 */
    private static int fromEthiopianDateToJDN(int year, int month, int day) {
        return ((JdOffset + 365)
                + ((365
                * (year - 1))
                + ((year / 4)
                + ((30 * month)
                + (day - 31)))));
    }

    
    /**reads the date of the client machine and change it to Ethiopian date
     *@return returns the current date in EC with 'yyyy/MM/dd' format
     */
    public static EthDateDTO TodayInEC(){
        Calendar c = Calendar.getInstance();
        return  ToEthiopianDate(c.getTime());
    }
	/**
	 * This method add days on the given ethiopian date 
	 * @param ethiopianDate a String date in yyyy/MM/dd format
	 * @param days an integer value of the day to be added
	 * @return String of ethiopian date with 'yyyy/MM/dd' format
	 * @throws Exception
	 */
    public static EthDateDTO addDate(String ethiopianDate, int days) throws Exception {
        EthDateDTO etDate = new EthDateDTO(ethiopianDate);

        Date gcDate = ToGregorianDate(etDate);
        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.DATE ,days);
        return  ToEthiopianDate(c.getTime());
    }

    public static EthDateDTO addDate(EthDateDTO etDate, int days) throws Exception {

        Date gcDate = ToGregorianDate(etDate);
        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.DATE ,days);
        return  ToEthiopianDate(c.getTime());
    }

    /**
     * This method add months on the given ethiopian date 
     * @param ethiopianDate a String date in yyyy/MM/dd format
     * @param month  an integer value of month to be added
     * @return String of ethiopian date with 'yyyy/MM/dd' format
     * @throws Exception
     */
    public static EthDateDTO addMonth(String ethiopianDate, int month) throws Exception {
        EthDateDTO etDate = new EthDateDTO(ethiopianDate);

        Date gcDate = ToGregorianDate(etDate);

        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.MONTH ,month);
        return  ToEthiopianDate(c.getTime());
    }

    public static EthDateDTO addMonth(EthDateDTO etDate, int month) throws Exception {

        Date gcDate = ToGregorianDate(etDate);

        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.MONTH ,month);
        return  ToEthiopianDate(c.getTime());
    }
	/**
	 * This method add years on the given ethiopian date 

	 * @param year an integer value of year to be added
	 * @return String of ethiopian date with 'yyyy/MM/dd' format
	 * @throws Exception
	 */
    public static EthDateDTO addYear(EthDateDTO etDate,int year) throws Exception {


        Date gcDate = ToGregorianDate(etDate);

        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.YEAR ,year);
        return  ToEthiopianDate(c.getTime());
    }

    public static EthDateDTO addYear(String ethiopianDate,int year) throws Exception {
        EthDateDTO etDate = new EthDateDTO(ethiopianDate);

        Date gcDate = ToGregorianDate(etDate);

        Calendar c = Calendar.getInstance();
        c.set(getYear(gcDate),getMonth(gcDate)-1,getDate(gcDate));
        c.add(Calendar.YEAR ,year);
        return  ToEthiopianDate(c.getTime());
    }
	/**
	 * This method find the date of birth using age
	 * @param age  an integer value of age to be converted into date of birth
	 * @return Date of birth in ethiopian date format
	 */
    public static EthDateDTO getDateOfBirthEC(int age){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR,-1*age);
        return  ToEthiopianDate(c.getTime());
    }
    public static Date getDateOfBirthGC(int age){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR,-1*age);
        return  c.getTime();
    }

    public static int calculateAge(Date dob){
        int age=0;

        LocalDate localDoB = LocalDate.of(getYear(dob),getMonth(dob),getDate(dob));
        LocalDate now = LocalDate.now();
        Period diff = Period.between(localDoB,now);
        age=diff.getYears();
        return  age;
    }

    public static int calculateAge(EthDateDTO dob) throws Exception {
        Date gcDob= ToGregorianDate(dob);
        return  calculateAge(gcDob);
    }

    /**
     * This method make an int day from a date
     * @param date 
     * @return an int day from a date
     */
    public static int getDate(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(date));
    }

    /**
     * This method make an int Month from a date
     * @param date 
     * @return an int Month from a date
     */
    public static int getMonth(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return Integer.parseInt(dateFormat.format(date));
    }


    /**
     * This method make an int Year from a date
     * @param date 
     * @return an int Year from a date
     */
    public static int getYear(Date date) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return Integer.parseInt(dateFormat.format(date));
    }
    /**This method calculates the 'year' difference between two date strings
     *and returns +ve if date1 > date2 else returns -ve
     *@param date1 is a String date (in yyyy-mm-dd )
     *@param date2 is a String date (in yyyy-mm-dd )
     *@see #differenceInDays
     *@return returns an int value which is the difference between the years
     */
    public static int differenceInYears(String date1, String date2) {
        String year1 = date1.substring(0, date1.indexOf('/'));
        String year2 = date2.substring(0, date2.indexOf('/'));
        int yearDiff = Integer.parseInt(year1) - Integer.parseInt(year2);

        return yearDiff;
    }

    /**This method calculates the 'month' difference between two date strings
     *and returns +ve if date1 > date2 else returns -ve
     *@param date1 is a String date (in yyyy-mm-dd )
     *@param date2 is a String date (in yyyy-mm-dd )
     *@see #differenceInDays
     *@return returns an int value which is the difference between the months
     *
     */
    public static int differenceInMonths(String date1, String date2) {
        String month1 = date1.substring(date1.indexOf('/') + 1, date1.lastIndexOf('/'));
        String month2 = date2.substring(date2.indexOf('/') + 1, date2.lastIndexOf('/'));
        int monthDiff = Integer.parseInt(month1) - Integer.parseInt(month2);

        return monthDiff;
    }

    /**This method calculates the 'day' difference between two date strings
     *and returns +ve if date1 > date2 else returns -ve
     *@param date1 is a String date (in yyyy-mm-dd )
     *@param date2 is a String date (in yyyy-mm-dd )

     *@return returns an int value which is the difference between the days
     *
     */
    public static int differenceInDays(String date1, String date2) {
        String day1 = date1.substring(date1.lastIndexOf('/') + 1);
        String day2 = date2.substring(date2.lastIndexOf('/') + 1);
        int dayDiff = Integer.parseInt(day1) - Integer.parseInt(day2);

        return dayDiff;
    }

    /**
     * This method compares two date strings and returns the difference
     * and +ve if date2 > date1 else returns -ve.
     * @param date1 is a String date (in yyyy-mm-dd )
     * @param date2 is a String date (in yyyy-mm-dd )
     * @return integer date difference in day
     */
    public static int compareDateDifference(String date1, String date2) {
        int yearDiff = differenceInYears(date2, date1);
        int monthDiff = differenceInMonths(date2, date1);
        int dayDiff = differenceInDays(date2, date1);
        int yearDiffInDays = 0;
        int monthDiffInDays = 0;
        int i = 0;
        int year = Integer.parseInt(date2.split("/")[0]);
        int preYear = Integer.parseInt(date1.split("/")[0]);
        int month = Integer.parseInt(date2.split("/")[1]);
        int day = Integer.parseInt(date2.split("/")[2]);
        for (int j = preYear; j < year; j++) {
            if ((j % 4 == 0)) {
                i = i + 1;
            }
        }
        if (month == 13 && day == 6) {
            i = i + 1;
        }
        yearDiffInDays = (yearDiff * 365) + i;
        monthDiffInDays = monthDiff * 30;
        int totalDiffInDays = yearDiffInDays + monthDiffInDays + dayDiff;
        return totalDiffInDays;
    }
    
    /**********************************   Tests   ************************************************/
    private static void testGCToEC() throws Exception {
        System.out.println("------------------------------------------------------------------");
        System.out.println("TESTING GC  to Ethiopian Date Conversion");
        System.out.println("------------------------------------------------------------------");
        Calendar cal = Calendar.getInstance();
    cal.set(1999,10,19);
        //for(int i=0;i<100;i++) {

        	EthDateDTO ethiopianDate =ToEthiopianDate(cal.getTime());

            System.out.println("GC :" + getYear(cal.getTime()) +"/" +getMonth(cal.getTime()) +"/"+getDate(cal.getTime()) +" --- ET :>"+ethiopianDate);
            //cal.add(Calendar.YEAR,1);
       // }
    }

    private static void testECtoGC() throws Exception {
        System.out.println("------------------------------------------------------------------");
        System.out.println("TESTING Ethiopian Date to GC Conversion");
        System.out.println("------------------------------------------------------------------");

        Calendar cal = Calendar.getInstance();
        for(int i=0;i<100;i++) {

        	EthDateDTO etDate = ToEthiopianDate(cal.getTime());

            Date date =ToGregorianDate(etDate);

            System.out.println("ET:" + etDate +" --- GC:>"+getYear(date) +"/" +getMonth(date) +"/"+getDate(date));
            cal.add(Calendar.YEAR,1);
        }
    }

    private static void testDateOperations() throws Exception {

        System.out.println("TESTING DATE OPERATIONS: ADD Date, Month, Year, Current Date, DoB");
        System.out.println("------------------------------------------------------------------");
        EthDateDTO today=TodayInEC();
        System.out.println("Today:"+today);
        System.out.println("Add 2 Days:"+addDate(today,2));
        System.out.println("Add 2 Months:"+addMonth(today,2));
        System.out.println("Add 2 Years:"+addYear(today,2));

        System.out.println("DoB of 35 Years:"+ getDateOfBirthEC(35));
    }
    public static void main(String[] ags) throws Exception {
        testGCToEC();
       // testECtoGC();
       // testDateOperations();
    }
}
