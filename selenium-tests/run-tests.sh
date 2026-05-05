#!/bin/bash

# Test Execution Script for Selenium Test Suite
# Usage: ./run-tests.sh [option]
# 
# Options:
#   all              Run all tests
#   login            Run LoginTest
#   register         Run RegisterTest  
#   doctor           Run DoctorManagementTest
#   patient          Run PatientManagementTest
#   appointment      Run ManageAppointmentsTest
#   dashboard        Run DashboardTest
#   quick            Run quick test suite (5 core features)
#   clean            Clean Maven build
#   help             Show help message

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_header() {
    echo -e "${BLUE}================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Show help
show_help() {
    cat << EOF
Test Execution Script for Selenium Test Suite

Usage: ./run-tests.sh [option]

Available Options:
  all              Run all tests (55+ test methods)
  login            Run LoginTest (9 tests)
  register         Run RegisterTest (4 tests)
  password         Run ChangePasswordTest (4 tests)
  doctor           Run DoctorManagementTest (4 tests)
  patient          Run PatientManagementTest (4 tests)
  appointment      Run ManageAppointmentsTest (5 tests)
  treatment        Run TreatmentPlanTest (3 tests)
  medical          Run MedicalRecordTest (3 tests)
  dashboard        Run DashboardTest (5 tests)
  
  quick            Run quick suite (5 core features)
  clean            Clean Maven build
  rebuild          Clean and rebuild
  help             Show this help message

Examples:
  ./run-tests.sh login          # Run login tests
  ./run-tests.sh all            # Run all tests
  ./run-tests.sh clean          # Clean build

Requirements:
  - Java 11+
  - Maven 3.6+
  - Application running at http://localhost/umbrella-corporation
  - MySQL database at localhost:3306

EOF
}

# Run all tests
run_all_tests() {
    print_header "Running All Tests (55+ test methods)"
    mvn clean test
    print_success "All tests completed!"
}

# Run specific test class
run_test_class() {
    local test_class=$1
    print_header "Running $test_class"
    mvn test -Dtest=$test_class
    print_success "$test_class completed!"
}

# Run quick test suite
run_quick_tests() {
    print_header "Running Quick Test Suite"
    echo "Features to test:"
    echo "  1. LoginTest (9 tests)"
    echo "  2. RegisterTest (4 tests)"
    echo "  3. DashboardTest (5 tests)"
    echo "  4. PatientManagementTest (4 tests)"
    echo "  5. DoctorManagementTest (4 tests)"
    echo ""
    
    mvn test -Dtest=LoginTest,RegisterTest,DashboardTest,PatientManagementTest,DoctorManagementTest
    print_success "Quick test suite completed!"
}

# Clean Maven build
clean_build() {
    print_header "Cleaning Maven Build"
    mvn clean
    print_success "Build cleaned!"
}

# Rebuild everything
rebuild() {
    print_header "Rebuilding Project"
    mvn clean install
    print_success "Rebuild completed!"
}

# Main script logic
case "${1:-help}" in
    all)
        run_all_tests
        ;;
    login)
        run_test_class "LoginTest"
        ;;
    register)
        run_test_class "RegisterTest"
        ;;
    password)
        run_test_class "ChangePasswordTest"
        ;;
    doctor)
        run_test_class "DoctorManagementTest"
        ;;
    patient)
        run_test_class "PatientManagementTest"
        ;;
    appointment)
        run_test_class "ManageAppointmentsTest"
        ;;
    treatment)
        run_test_class "TreatmentPlanTest"
        ;;
    medical)
        run_test_class "MedicalRecordTest"
        ;;
    dashboard)
        run_test_class "DashboardTest"
        ;;
    quick)
        run_quick_tests
        ;;
    clean)
        clean_build
        ;;
    rebuild)
        rebuild
        ;;
    help)
        show_help
        ;;
    *)
        echo "Unknown option: $1"
        show_help
        exit 1
        ;;
esac

exit 0
