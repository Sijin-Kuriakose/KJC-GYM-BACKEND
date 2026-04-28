package in.edu.kristujayanti.propertyBinder;


import in.edu.kristujayanti.propertyBinder.Eform.Applicant.ApplicantFormsKeysPBinder;
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
import in.edu.kristujayanti.propertyBinder.Ticketing.TicketingKeysPBinder;
import in.edu.kristujayanti.propertyBinder.academics.AcademicsCommonKeysPBinder;
import in.edu.kristujayanti.propertyBinder.arena.*;
import in.edu.kristujayanti.propertyBinder.core.ERPUserProfileKeysPBinder;
import in.edu.kristujayanti.propertyBinder.core.MenuPBinder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PropertyEnumRegistry {
    private static final Map<String, Class> KJUSYS_ENUM_REGISTRY = new ConcurrentHashMap();

    public static Class getEnumClass(String key) {
        if (KJUSYS_ENUM_REGISTRY.containsKey(key)) {
            return KJUSYS_ENUM_REGISTRY.get(key);
        } else {
            throw new IllegalStateException("Registry key " + key + " is invalid");
        }
    }

    static {
        KJUSYS_ENUM_REGISTRY.put("EformCommon", EformCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("AuthCommon" , AuthCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("ApplicantForms", ApplicantFormsKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("KJUSYSCommon", KJUSYSCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("ERPUserProfile", ERPUserProfileKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("EmployeeOnBoarding", OnBoardingKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("RazorpayKeys", RazorpayKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("EmployeePaySlip", PaySlipGenerationKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("LibraryCheckInAndOut", LibraryCheckInAndOutKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("LibraryCommon", LibraryCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("LibraryBook", LibraryBookKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("ERPStaffUserProfile", LibraryCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("ERPStudentUserProfile", LibraryCommonKeysPBinder.class);

        KJUSYS_ENUM_REGISTRY.put("QueueManager", QueueManagerKeysPBinder.class);

        KJUSYS_ENUM_REGISTRY.put("Menu", MenuPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("FeeModule", FeeModuleKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("Ticketing", TicketingKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("AcademicsCommon", AcademicsCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("HRCommon", HRCommonKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("EmailSenderCommon", EmailSenderCommonKeysPBinder.class);

        KJUSYS_ENUM_REGISTRY.put("AttendanceData", AttendanceDataKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("BulkBooking", BulkBookingKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("VenueCreation", CreateVenueKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("FetchVenue", FetchVenueKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("Report", GetAllBookingsKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("OthersBooking", OthersBookingKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("UserBooking", UserBookingKeysPBInder.class);
        KJUSYS_ENUM_REGISTRY.put("WorkShift", WorkShiftKeysPBinder.class);

        KJUSYS_ENUM_REGISTRY.put("InterviewKeys", InterviewKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("PrincipalViewKeys", PrincipalViewKeysPBinder.class);

        KJUSYS_ENUM_REGISTRY.put("StaffRoleKeysPBinder", StaffRoleKeysPBinder.class);



        KJUSYS_ENUM_REGISTRY.put("LeaveApplications", LeaveApplicationsKeysPBinder.class);
        KJUSYS_ENUM_REGISTRY.put("AwardsDataKeys", AwardsDataKeyPBinders.class);
        KJUSYS_ENUM_REGISTRY.put("UpdateVenue", VenueUpdateKeysPBInder.class);

        KJUSYS_ENUM_REGISTRY.put("Gym", GymKeysPBinder.class);

    }
}