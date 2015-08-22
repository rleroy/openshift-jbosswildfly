Feature: Combining images into a bigger one.

Scenario: Combine 2 images
Given left image  is "left.png"
Given right image is "right.png"
 When I combine both images left to right
 Then target image is generated with the right size
