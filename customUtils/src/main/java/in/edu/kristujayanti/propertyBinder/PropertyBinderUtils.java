package in.edu.kristujayanti.propertyBinder;

import in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantFormsKeysPBinder;
import in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantProgrammeKeysPBinder;
import in.edu.kristujayanti.propertyBinder.Eform.EformCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.FeeModule.FeeModuleKeysPBinder;
import in.edu.kristujayanti.propertyBinder.Gym.GymKeysPBinder;
import in.edu.kristujayanti.propertyBinder.HR.*;
import in.edu.kristujayanti.propertyBinder.Library.LibraryBookKeysPBInder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCheckInAndOutKeysPBinder;
import in.edu.kristujayanti.propertyBinder.Library.LibraryCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.QueueManager.QueueManagerKeysPBinder;
import in.edu.kristujayanti.propertyBinder.RazorpayPayment.RazorpayKeysPBinder;
import in.edu.kristujayanti.propertyBinder.SIM.InterviewKeysPBinder;
import in.edu.kristujayanti.propertyBinder.academics.AcademicsCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.arena.*;
import in.edu.kristujayanti.propertyBinder.core.ERPUserProfileKeysPBinder;
import in.edu.kristujayanti.propertyBinder.core.MenuPBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to fetch display names by property names across various property binder enums.
 */
public class PropertyBinderUtils {

    /**
     * Retrieves the display name associated with the given property name across all property binder enums.
     *
     * @param propertyName The property name for which the display name is requested.
     * @return The display name corresponding to the property name, or null if not found.
     */
    public static String getDisplayNameByPropertyName(String propertyName) {
        List<Class<? extends KJUSYSPropertyBinder>> binderClasses = getAllPropertyBinderClasses();

        // Iterate through each property binder enum class
        for (Class<? extends KJUSYSPropertyBinder> binderClass : binderClasses) {
            KJUSYSPropertyBinder[] binders = binderClass.getEnumConstants();
            if (binders != null) {
                // Iterate through each enum constant within the binder
                for (KJUSYSPropertyBinder binder : binders) {
                    // Compare property names to find a match
                    if (binder.getPropertyName().equals(propertyName)) {
                        return binder.getDisplayName(); // Return display name if match found
                    }
                }
            }
        }

        return null; // Return null if no matching property name found
    }

    /**
     * Retrieves the property name associated with the given display name across all property binder enums.
     *
     * @param displayName The display name for which the property name is requested.
     * @return The property name corresponding to the display name, or null if not found.
     */
    public static String getPropertyNameByDisplayName(String displayName) {
        // Get a list of all property binder enum classes
        List<Class<? extends KJUSYSPropertyBinder>> binderClasses = getAllPropertyBinderClasses();

        // Iterate through each property binder enum class
        for (Class<? extends KJUSYSPropertyBinder> binderClass : binderClasses) {
            // Get all enum constants for the current binder class
            KJUSYSPropertyBinder[] binders = binderClass.getEnumConstants();
            if (binders != null) {
                // Iterate through each enum constant within the binder
                for (KJUSYSPropertyBinder binder : binders) {
                    // Compare display names to find a match
                    if (binder.getDisplayName().equals(displayName)) {
                        // Return property name if match found
                        return binder.getPropertyName();
                    }
                }
            }
        }

        // Return null if no matching display name found
        return null;
    }


    /**
     * Retrieves all property binder enum classes that implement the KJUSYSPropertyBinder interface.
     *
     * @return A list of Class objects representing property binder enum classes.
     */
    private static List<Class<? extends KJUSYSPropertyBinder>> getAllPropertyBinderClasses() {
        // Return a list of all relevant property binder enum classes
        return List.of(
                AuthCommonKeysPBinder.class,
                KJUSYSCommonKeysPBinder.class,
                ApplicantFormsKeysPBinder.class,
                ApplicantProgrammeKeysPBinder.class,
                EformCommonKeysPBinder.class,
                ERPUserProfileKeysPBinder.class,
                OnBoardingKeysPBinder.class,
                PaySlipGenerationKeysPBinder.class,
                LibraryCheckInAndOutKeysPBinder.class,
                LibraryBookKeysPBInder.class,
                LibraryCommonKeysPBinder.class,
                GymKeysPBinder.class,
                QueueManagerKeysPBinder.class,

                MenuPBinder.class,
                FeeModuleKeysPBinder.class,
                AttendanceDataKeysPBinder.class,
                FeeModuleKeysPBinder.class,
                AcademicsCommonKeysPBinder.class,
                HRCommonKeysPBinder.class,

                EmailSenderCommonKeysPBinder.class,
                RazorpayKeysPBinder.class,

                WorkShiftKeysPBinder.class,

                CreateVenueKeysPBInder.class,
                        FetchVenueKeysPBInder.class,
                BulkBookingKeysPBInder.class,
                UserBookingKeysPBInder.class,
                GetAllBookingsKeysPBInder.class,
                OthersBookingKeysPBInder.class,

                InterviewKeysPBinder.class,
                VenueUpdateKeysPBInder.class

        );
    }

    /**
     * Retrieves a list of property names for a given property binder enum.
     *
     * @param propertyBinder The property binder enum.
     * @return A list of property names.
     */
    public static List<String> getPropertyNames(Class<? extends KJUSYSPropertyBinder> propertyBinder) {
        List<String> propertyNames = new ArrayList<>();
        KJUSYSPropertyBinder[] keys = propertyBinder.getEnumConstants();
        if (keys != null) {
            for (KJUSYSPropertyBinder key : keys) {
                propertyNames.add(key.getPropertyName());
            }
        }
        return propertyNames;
    }
}