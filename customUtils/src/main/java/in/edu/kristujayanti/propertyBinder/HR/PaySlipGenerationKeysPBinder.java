package in.edu.kristujayanti.propertyBinder.HR;

import in.edu.kristujayanti.propertyBinder.KJUSYSPropertyBinder;

public enum PaySlipGenerationKeysPBinder implements KJUSYSPropertyBinder {

    CREATED_BY("createdBy_EmployeePaySlip_Text", "Payslip Created By", String.class),
    MODIFIED_BY("modifiedBy_EmployeePaySlip_Text", "Payslip Modified By", String.class),
    PERFORMED_BY("performedBy_EmployeePaySlip_Text", "Payslip Modified By", String.class),
    UPLOADED_FILENAME("uploadedFilename_EmployeePaySlip_Text", "Payslip Modified By", String.class),
    ACTIVITY_TIMESTAMP("activityTimestamp_EmployeePaySlip_DateTime", "Activity Timestamp ", Long.class),
    SALARY_YEAR("salaryYear_EmployeePaySlip_Text", "Employee PaySlip Year", String.class),
    SALARY_MONTH("salaryMonth_EmployeePaySlip_Text", "Employee PaySlip Month", String.class),
    SALARY_MONTH_NUMBER_OF_DAYS("salaryMonthNumberOfDays_EmployeePaySlip_Text", "Number of Days", String.class),
    EMPLOYEE_LOP("employeeLop_EmployeePaySlip_Text", "LOP Days", String.class),
    EMPLOYEE_LOP_AMOUNT("employeeLopAmount_EmployeePaySlip_Text", "Loss Of Pay", String.class),
    EMPLOYEE_BASIC_PAY("employeeBasicPay_EmployeePaySlip_Currency", "Basic", Double.class),
    EMPLOYEE_GRADE_PAY("employeeGradePay_EmployeePaySlip_Currency", "Grade", Double.class),
    EMPLOYEE_DA("employeeDa_EmployeePaySlip_Currency", "DA", Double.class),
    EMPLOYEE_HRA("employeeHra_EmployeePaySlip_Currency", "HRA", Double.class),
    EMPLOYEE_RESEARCH_ALLOWANCE("employeeResearchAllowance_EmployeePaySlip_Currency", "Research Allowance", Double.class),

    //EMPLOYEE_SPCL_ALLOWANCE("employeeSpclAllowance_EmployeePaySlip_Currency", "Special Allowance", Double.class),
    //EMPLOYEE_CAMPUS_ALLOWANCE("employeeCampusAllowance_EmployeePaySlip_Currency", "Campus Allowance", Double.class),
    EMPLOYEE_PROVIDENT_FUND("employeeProvidentFund_EmployeePaySlip_Currency", "PF", Double.class),
    EMPLOYEE_PROVIDENT_FUND_EMPLOYEE_SHARE("employeeProvidentFundEmployeeShare_EmployeePaySlip_Currency", "PF (Employee Share)", Double.class),
    EMPLOYEE_PT("employeePt_EmployeePaySlip_Currency", "PT", Double.class),
    EMPLOYEE_TDS("employeeTds_EmployeePaySlip_Currency", "TDS", Double.class),
    EMPLOYEE_LOAN_DEDUCTION("employeeLoanDeduction_EmployeePaySlip_Currency", "Loan Deduction", Double.class),
    //EMPLOYEE_PENSION_CONTRIBUTION("employeePensionContribution_EmployeePaySlip_Currency", "Employee Pension Contribution", Double.class),
    EMPLOYEE_SODEXO("employeeSodexo_EmployeePaySlip_Currency", "Sodexo", Double.class),
    EMPLOYEE_OTHER_DEDUCTION("employeeOtherDeduction_EmployeePaySlip_Currency", "Other Deductions", Double.class),
    //EMPLOYEE_NET_SALARY("employeeNetSalary_EmployeePaySlip_Currency","Employee Net Salary", Double.class),
    EMPLOYEE_MANAGEMENT_CONTRIBUTION_PF("employeeManagementContributionPf_EmployeePaySlip_Currency", "PF (Management Contribution)", Double.class),
    EMPLOYEE_MANAGEMENT_CONTRIBUTION_ESI("employeeManagementContributionEsi_EmployeePaySlip_Currency", "Employee Management Contribution ESI", Double.class),
    //TOTAL_COST_TO_INSTITUTION("totalCostToInstitution_EmployeePaySlip_Currency", "Total Cost To Institution", Double.class),
    TOTAL_EMPLOYEE_GROSS_SALARY("totalEmployeeGrossSalary_EmployeePaySlip_Currency", "Gross", Double.class),
    TOTAL_EMPLOYEE_DEDUCTIONS("totalEmployeeDeductions_EmployeePaySlip_Currency", "Total Deductions", Double.class),
    TOTAL_EMPLOYEE_NET_SALARY("totalEmployeeNetSalary_EmployeePaySlip_Currency", "Net Salary", Double.class),

    EMPLOYEE_NPS("employeeNps_EmployeePaySlip_Currency", "NPS (Employee Share)", Double.class),
    EMPLOYEE_OTHER_ALLOWANCES("employeeOtherAllowances_EmployeePaySlip_Currency", "Other Allowances(Spl Allowance, Campus Allowance)", Double.class),
    EMPLOYEE_MANAGEMENT_CONTRIBUTION_NPS("employeeManagementContributionNps_EmployeePaySlip_Currency", "NPS (Management Contribution)", Double.class),
    EMPLOYEE_ADVANCE_TDS("employeeAdvanceTds_EmployeePaySlip_Currency", "Advance TDS", Double.class),
    EMPLOYEE_STAFF_INTERNATIONAL_EXPOSURE_PROGRAM("employeeStaffInternationalExposureProgram_EmployeePaySlip_Currency", "Employee Staff International Exposure Program", Double.class);


    private final String property;
    private final String label;
    private final Class<?> dataType;
    private final int minLength;
    private final int maxLength;

    private PaySlipGenerationKeysPBinder(String property, String label, Class<?> dataType, int minLength, int maxLength) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    private PaySlipGenerationKeysPBinder(String property, String label, Class<?> dataType) {
        this.property = property;
        this.label = label;
        this.dataType = dataType;
        this.minLength = -1;
        this.maxLength = -1;
    }

    public String getPropertyName() {
        return this.property;
    }

    public String getDisplayName() {
        return this.label;
    }

    public Class<?> getDataType() {
        return this.dataType;
    }

    public Integer getMinLength() {
        return this.minLength;
    }

    public Integer getMaxLength() {
        return this.maxLength;
    }

    // $FF: synthetic method
    private static PaySlipGenerationKeysPBinder[] $values() {
        return PaySlipGenerationKeysPBinder.class.getEnumConstants();
    }
}
