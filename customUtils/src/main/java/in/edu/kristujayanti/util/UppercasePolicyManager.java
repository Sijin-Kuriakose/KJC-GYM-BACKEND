package in.edu.kristujayanti.util;


import java.util.HashMap;
import java.util.Map;

public class UppercasePolicyManager {

    public enum UppercasePolicy {
        UPPERCASE,
        PRESERVE_CASE
    }

    private static final Map<String, UppercasePolicy> policyMap = new HashMap<>();

    static {
        // Initialize the map with default policies
        policyMap.put("applicantPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("applicantOldPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("password", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("oldPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("newPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("confirmPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("userPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("uploadedFilePath", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("programmePreference2", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("programmePreference3", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("programName", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("programDisplayName", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("menuGroupIcon", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("studentProfileImageFilePath", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("employeeIdPhotoPath", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("employeeOldPassword", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("tenthPdf", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("bankAccountRazorpayId", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("razorpayPaymentId", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("applicantFeeDescription", UppercasePolicy.PRESERVE_CASE);
        //SIM
        policyMap.put("remarksModeOfEducation", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksSecondLanguage", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksCoCurricularActivities", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksSubjectKnowledge", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksAttitude", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksCommunication", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("remarksRemark", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("applicantPhoto", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("applicantPhysicallyChallengedDescription", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("eleventhPdf", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("twelfthPdf", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s1MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s2MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s3MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s4MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s5MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s6MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s7MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s8MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s9MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);
        policyMap.put("s10MarksDocumentUpload", UppercasePolicy.PRESERVE_CASE);


    }

    /**
     * Get the uppercase policy for a given property name.
     *
     * @param propertyName The name of the property to check.
     * @return The UppercasePolicy for the given property. If no specific policy is set, returns UPPERCASE.
     */
    public static UppercasePolicy getPolicy(String propertyName) {
        return policyMap.getOrDefault(propertyName, UppercasePolicy.UPPERCASE);
    }

    /**
     * Set a specific uppercase policy for a property.
     *
     * @param propertyName The name of the property.
     * @param policy The UppercasePolicy to set for this property.
     */
    public static void setPolicy(String propertyName, UppercasePolicy policy) {
        policyMap.put(propertyName, policy);
    }

    /**
     * Remove the specific policy for a property, reverting it to the default (UPPERCASE).
     *
     * @param propertyName The name of the property to remove the specific policy for.
     */
    public static void removePolicy(String propertyName) {
        policyMap.remove(propertyName);
    }

    /**
     * Clear all specific policies, reverting all properties to the default (UPPERCASE).
     */
    public static void clearAllPolicies() {
        policyMap.clear();
    }
}