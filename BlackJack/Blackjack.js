
var userMoney;
var betMoney;
var dealerTotal;
var playerTotal;
var deckId;
var userBusted;;
var dealerCardArray = [];
var playerCardArray = [];

// it would probably make sense to put a 6-pack deck of cards into a database with suit, value, and code
// do as much as possible in the server layer, then pass it to the deckOfCards API to get the images
// just seems slow to go back and forth with ajax calls between two servers

// first need to verify who won if user does not already bust
// need to show dealCardButton after a round ends (bust or win)
// need to focus on going consecutive hands of play now
// HIDE DEALER CARD 2 (wait to show image from dealer card array)

// should add an onload event that clears all previous values (in the case of a mid-hand refresh)
$(document).ready(function () {
    userMoney = 0.00;
    userMoney = parseFloat(userMoney);
    betMoney = 0.00;
    var dealerTotal = 0;
    var playerTotal = 0;
    var deckId;
    var userBusted = false;
});

createAndShuffleDeck();

// if user doesn't have enough money to make that bet, throw message, otherwise add to text input
function addBet(betAmount) {
    clearMessages();
    betAmount = parseFloat(betAmount);
    betInputValue = $('#betAmount').val();
    betInputValue = parseFloat(betInputValue);

    Number.isNaN(betInputValue) ? betTotal = betAmount : betTotal = betAmount + betInputValue;

    if (betTotal > userMoney) {
        $('#messageDiv').css({ "background-color": "gold", "color": "black" });
        $('#messageDiv').append("You don't have that much money. Please try again.");
        $('#betAmount').val('');
        betTotal = 0;
    } else {
        $('#betAmount').val('')
        betMoney += betAmount;
        $('#betAmount').val(betMoney);
    }
};

function addMoney() {
    $('#totalUserMoney').empty();
    isNumber = false;

    while (!isNumber) {
        var addedMoney = prompt("How much money would you like to add?", "added amount")
        addedMoney = parseFloat(addedMoney);
        if (isNaN(addedMoney) || addedMoney == null || addedMoney <= 0) {
            alert("Please enter a valid number!");
        } else {
            userMoney = parseFloat(userMoney);
            userMoney += addedMoney;
            userMoney = ((userMoney * 100) / 100).toFixed(2);

            $('#totalUserMoney').append("Total $: " + userMoney + '<br/>');
            addedMoney = 0;
            clearMessages();
            isNumber = true;
        }
    }
};

function clearBet() {
    betMoney = 0.00;
    $('#betAmount').val('');
}

function clearMessages() {
    $('#messageDiv').empty();

}

function clearCards() {
    $('#dealerCardsDiv').empty();
    $('#playerCardsDiv').empty();
    $('#dealerTotal').empty();
    $('#playerTotal').empty();
    // userMoney = 0.00;
    // userMoney = parseFloat(userMoney);
    // betMoney = 0.00;
    dealerTotal = 0;
    playerTotal = 0;
    var userBusted = false;

    // for (var i = 0; i < dealerCardArray.length; i++) {
    //     dealerCardArray.pop();
    // }

    // for (var j = 0; j < playerCardArray.length; j++) {
    //     playerCardArray.pop();
    // }
};

// hook up to the API with a get request to make a deck (6 decks) and shuffle
function createAndShuffleDeck() {
    $.ajax({
        type: 'GET',
        url: 'https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=6',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        datatype: 'json',
        success: function (deck) {
            deckId = deck.deck_id;
        },
        error: function () {
            alert("Failed to load and shuffle new deck of cards");
        }
    })
};

// for four cards to start the game and change buttons
function dealCards() {

    clearCards(); // might need to move this to the post win/lose/tie method once made

    if (betMoney >= 1) {
        $.ajax({
            type: 'GET',
            url: 'https://deckofcardsapi.com/api/deck/' + deckId + '/draw/?count=4',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            datatype: 'json',
            success: function (cardArray) {

                $('#dealCardButtonRow').hide();
                $('#betAmountRow').hide();
                $('#betRow').hide();

                playerLocation = '#playerCardsDiv';

                // show the hit / stay / double / split buttons
                $('#hitButton').show();
                $('#stayButton').show();
                $('#doubleDownButton').show();
                $('#splitButton').show();
                $('#cashoutButton').hide();
                $('#addMoneyButton').hide();
                $('#otherButtonRow').show();
                $('#dealCardsButtonRow').hide();


                var card1 = cardArray.cards[0];
                var card2 = cardArray.cards[1];
                var card3 = cardArray.cards[2];
                var card4 = cardArray.cards[3];

                // insertcards into dealer and player arrays
                playerCardArray.push(card1, card2);
                dealerCardArray.push(card3, card4);

                card1img = card1.image;
                card2img = card2.image;
                card3img = card3.image;
                card4img = card4.image;

                showImage(card1img, '#playerCardsDiv', 136, 188);
                showImage(card2img, '#playerCardsDiv', 136, 188);
                showImage(card3img, '#dealerCardsDiv', 136, 188);
                showImage(card4img, '#dealerCardsDiv', 136, 188);

                playerTotal = calculateTotal(playerCardArray, '#playerTotal');
                $('#playerTotal').append(playerTotal);

                dealerTotal = calculateTotal(dealerCardArray, '#dealerTotal');
                $('#dealerTotal').append(dealerTotal);
            },
            error: function () {
                alert("failed to deal cards!");
            }
        })
    }
    else {
        alert("You must enter a bet before cards can be dealt");
    }
};

// cardLocation defines whether it is the dealer or player receiving the new card
function hit(cardLocation, cardArray) {
    $.ajax({
        type: 'GET',
        url: 'https://deckofcardsapi.com/api/deck/' + deckId + '/draw/?count=1',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        datatype: 'json',
        success: function (returnedCardArray) {

            // run through array of cards in hand and show each
            for (var i = 0; i < returnedCardArray.cards.length; i++) {
                var cardToCount = returnedCardArray.cards[i];
                cardImg = cardToCount.image;
                showImage(cardImg, cardLocation, 136, 188);
                cardArray.push(cardToCount);
            }
            calculateTotal(cardArray, '#playerTotal');
        },
        error: function () {
            alert("failed to deal card!");
        }
    })
};

function showImage(src, location, width, height, alt) {
    var img = document.createElement("img");
    img.src = src;
    img.width = width;
    img.height = height;
    img.alt = alt;

    $(location).append(img);
};

function calculateTotal(cardArrayToCal, location) {

    $.ajax({
        type: 'POST',
        url: 'http://localhost:8080/blackjack/api/calc',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: JSON.stringify(cardArrayToCal),
        datatype: 'json/application',
        success: function (cardValue) {

            cardValue = parseInt(cardValue, 10);
            $(location).empty();
            $(location).append(cardValue);

            if (location == '#playerTotal') {
                playerTotal = cardValue;
                validateBust(playerTotal, 'Player');

            } else {
                dealerTotal = cardValue;
                validateBust(dealerTotal, 'Dealer');
            }
            cardValue = 0;
        },
        error: function () {
            alert("failed to connect to blackjack server");
        }
    })
};

// SHOULD INCLUDE (COUNT, PLAYER) AS PARAMETERS SO THAT I CAN ALTER THE MESSAGE TO YOU BUSTED OR DEALER BUSTED
// Just add to the if statement if (userBusted && player = player)
function validateBust(count, player) {
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/blackjack/api/validateBust/' + count,
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        data: 'application/json',
        datatype: 'application/json',
        success: function (boolean) {
            userBusted = boolean;
            if (userBusted && player == 'Player') {
                $('#messageDiv').empty();
                $('#messageDiv').append("You BUSTED with " + playerTotal);
                userMoney = (userMoney - betMoney).toFixed(2);
                $('#totalUserMoney').empty();
                $('#totalUserMoney').append("Total $: " + userMoney + '<br/>');

                clearCards();
                clearBet();

                $('#stayButton').hide();
                $('#hitButton').hide();
                $('#doubleDownButton').hide();
                $('#splitButton').hide();

                $('#cashoutButton').show();
                $('#addMoneyButton').show();
                $('#betAmountRow').show();
                $('#betRow').show();
                $('#otherButtonRow').show();
                $('#dealCardButtonRow').show();
            }

            if (player == 'Dealer') {
                // don't need to do anything because dealerTotal was already sent in as the count
            }
        },
        error: function () {
            alert("failed to validate count");
        }
    })
};

function cashout() {
    clearCards();
    clearBet();
    clearMessages();
    $('#messageDiv').append("Thank you for playing!");
};

function dealerHitOrStay() {
    // send JSON out to server with the dealer cardArray

    $.ajax({
        method: 'GET',
        url: 'http://localhost:8080/blackjack/api/dealerVal/' + dealerTotal,
        datatype: 'text',
        success: function (hitOrStay, textStatus, jqXHR) {
            // alert("success! Dealer function started");
            var dealerKeepHitting = true;

            while (dealerKeepHitting) {
                // calculateTotal(dealerCardArray, '#dealerTotal'); --> already included in hit function

                if (hitOrStay == "hit" && textStatus == "success") {
                    hit('#dealerCardsDiv', dealerCardArray);
                    // dealerHitOrStay();
                }
                else {
                    dealerKeepHitting = false;
                }
            }

        },
        error: function (request, error) {
            alert("failed to run hitOrStay function for the dealer");
        },
        complete: function () {
            determineWinner(dealerCardArray, playerCardArray);
            //need to do this after this loop is officially done
            // }
        }
    })

};

// send dealercardarray and playercardarray to server and determine winner, receive object with message and multiplier for winnings (1.5x for blackjack)
function determineWinner(dealerCardArray, playerCardArray) {

    // dealerArray = [];
    // playerArray=[];
    // for (var k = 0; k < dealerCardArray.length; k++){
    //     dealerArray.push(dealerCardArray[k].value)
    // }

    // for (var m = 0; m < playerCardArray.length; m++){
    //     playerArray.push(playerCardArray[m].value)
    // }

    var allCards = {
        dealer: dealerCardArray,
        player: playerCardArray
    };

    $.ajax({
        method: 'POST',
        url: 'http://localhost:8080/blackjack/api/determineWinner/',
        headers: {
            // 'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        //        data: '["1", "2", "3", "4"]',  this works
        // data: JSON.stringify(dealerCardArray),
        data: JSON.stringify(allCards),
        datatype: 'application/x-www-form-urlencoded;charset=UTF-8',
        success: function (message, textStatus, jqXHR) {
            alert("successfully connected to server to determine winner");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            alert('failed to connect to server to determine winner');
        }
    })
}

            // {
            //     "success": true,
            //     "cards": [
            //         {
            //             "image": "https://deckofcardsapi.com/static/img/KH.png",
            //             "value": "KING",
            //             "suit": "HEARTS",
            //             "code": "KH"
            //         },
            //         {
            //             "image": "https://deckofcardsapi.com/static/img/8C.png",
            //             "value": "8",
            //             "suit": "CLUBS",
            //             "code": "8C"
            //         }
            //     ],
            //     "deck_id":"3p40paa87x90",
            //     "remaining": 50
            // }

                            //ATTEMPT AT PROMISES...
                // var promise = new Promise(function(resolve, reject){
                //     reject('Promise rejected');
                //     if (userBusted == true || userBusted == false){
                //         resolve();
                //     } else {
                //         reject();
                //     }
                // })
                // promise.then(validateBust(playerTotal));