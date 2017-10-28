*** This code is intended for binary classification problems.
*** All of the attributes are numeric.
*** The neural network has connections between input and the hidden layer, and between the hidden and output layer and one bias unit and one output node.
*** The number of units in the hidden layer are equal to the number of input units.
*** For training the neural network,it uses n-fold stratified cross validation.
*** Uses sigmoid activation function and train using stochastic gradient descent.
*** Randomly sets initial weights for all units including bias in the range (-1,1).
*** Uses a threshold value of 0.5. If the sigmoidal output is less than 0.5, takes the prediction to be the class listed first in the ARFF file in the class      attributes section; else takes the prediction to be the class listed second in the ARFF file.
*** Uses Cross Entropy as cost function


Execute as “unit.uwm.cs760.nn.NNDriver trainfile num_folds learning_rate num_epochs “
