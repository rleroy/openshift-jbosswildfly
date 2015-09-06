Feature: Asynchronized cache service

Scenario: Read once
Given an asynchronized cache service
Given data is ok
Given file system data is fresh
Given memory data is empty
 When I access 10 time the data at once
 Then data should have been read once

Scenario: Load once
Given an asynchronized cache service
Given data is ok
Given file system data is empty
Given memory data is empty
 When I access 10 time the data at once
 Then data should have been loaded once
 Then data should have been written once

Scenario: Loading error
Given an asynchronized cache service
Given data is ko
Given file system data is empty
Given memory data is empty
 When I access the data
 Then I should get the response empty
  And I should get the response empty
  And the number of call to the loader should be 1
 
Scenario Outline: Cache service behaviour
Given an asynchronized cache service
Given data is <data>
Given file system data is <fs>
Given memory data is <mem>
 When I access the data
 Then I should get the response <response>
 Then the memory should be <memstatus>
 Then the file system should be <fsstatus>
 Then the number of call to the loader should be <call>

Examples:
  | data | fs      | mem     | response | memstatus | fsstatus | call |
  | ok   | fresh   | fresh   | fresh    | fresh     | fresh    | 0    |
  | ok   | fresh   | empty   | fresh    | fresh     | fresh    | 0    |
  | ok   | fresh   | expired | fresh    | fresh     | fresh    | 0    |
  | ok   | empty   | fresh   | fresh    | fresh     | empty    | 0    |
  | ok   | empty   | empty   | fresh    | fresh     | fresh    | 1    |
  | ok   | empty   | expired | fresh    | fresh     | fresh    | 1    |
  | ok   | expired | fresh   | fresh    | fresh     | expired  | 0    |
  | ok   | expired | empty   | fresh    | fresh     | fresh    | 1    |
  | ok   | expired | expired | fresh    | fresh     | fresh    | 1    |
  | ko   | fresh   | fresh   | fresh    | fresh     | fresh    | 0    |
  | ko   | fresh   | empty   | fresh    | fresh     | fresh    | 0    |
  | ko   | fresh   | expired | fresh    | fresh     | fresh    | 0    |
  | ko   | empty   | fresh   | fresh    | fresh     | empty    | 0    |
  | ko   | empty   | empty   | empty    | empty     | empty    | 1    |
  | ko   | empty   | expired | expired  | expired   | expired  | 1    |
  | ko   | expired | fresh   | fresh    | fresh     | expired  | 0    |
  | ko   | expired | empty   | expired  | expired   | expired  | 1    |
  | ko   | expired | expired | expired  | expired   | expired  | 1    |

 