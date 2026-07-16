@component @core
Feature: Gym core service component API

  @trainee-register
  Scenario: Register trainee successfully
    When trainee is registered through core service
    Then response status is 200
    And response contains generated credentials

  @auth-login
  Scenario: Reject invalid login credentials
    When user logs in with invalid credentials
    Then response status is 401
    And response contains error body

  @training-types
  Scenario: Reject forbidden training types request
    When training types are requested without authorization
    Then response status is 403