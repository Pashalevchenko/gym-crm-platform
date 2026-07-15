@component @workload
Feature: Workload service component API

  @workload-update
  Scenario: Update trainer workload successfully
    Given an authenticated gym user
    When trainer workload is updated through workload service
    Then the response status is 200

  @workload-get
  Scenario: Get trainer monthly workload successfully
    Given an authenticated gym user
    When trainer workload is updated through workload service
    Then the response status is 200
    When trainer monthly workload is requested through workload service
    Then the response status is 200
    And the workload response contains duration 45

  @workload-validation
  Scenario: Reject invalid workload update request
    Given an authenticated gym user
    When invalid trainer workload is sent through workload service
    Then the response status is 400
    And the response contains an error body

  @workload-security
  Scenario: Reject unauthorized workload request
    When trainer workload is requested without authorization
    Then the response status is 403