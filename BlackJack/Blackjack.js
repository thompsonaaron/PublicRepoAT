
var userMoney;
var betMoney;
var dealerTotal;
var playerTotal;
var deckId;
var userBusted;;
var dealerCardArray = [];
var playerCardArray = [];


// 1) add "Play Again" / dealCards button and show "cashout" button at the end of a round
// 2) replace the first dealer card with a back of the bicycle card
// 3) don't show dealer total until user clicks "stay" button
// 4) make setup responsive by settings md-3. Consider moving buttons on left of screen to top center
// 5) leave space for all rows to begin so there is not resizing every time a message pops up

// Once a user hits "stay" the "hit", "double down" and "split" buttons should toggle to disabled
// HIDE DEALER CARD 2 (wait to show image from dealer card array)
// add button to get count for card counting practice

// should add an onload event that clears all previous values (in the case of a mid-hand refresh)
$(document).ready(function () {
    userMoney = 0.00;
    userMoney = parseFloat(userMoney);
    betMoney = 0.00;
    var dealerTotal = 0;
    var playerTotal = 0;
    var deckId;
    var userBusted = false;
    dealerCardArray = [];
    playerCardArray = [];
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
        if (isNaN(addedMoney) || addedMoney == null || addedMoney < 0) {
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
    dealerTotal = 0;
    playerTotal = 0;
    dealerCardArray.empty;
    playerCardArray.empty;
    userBusted = false;
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

function hitSuper(cardLocation, cardArray, location) {
    return new Promise((resolve, reject) => {
        hit(cardLocation, cardArray).then(cardArray => {
            calculateTotal(cardArray, location);
            resolve(cardArray);
        })
    })
}

// cardLocation defines whether it is the dealer or player receiving the new card
function hit(cardLocation, cardArray) {
    return new Promise((resolve, reject) => {
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
                resolve(cardArray);
            },
            error: function () {
                alert("failed to deal card!");
                reject(error);
            }
        })
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
                toggleHitButtons();
                showStartButtons();
            }
        },
        error: function () {
            alert("failed to validate count");
        }
    })
};

function showStartButtons(){
    $('#cashoutButton').show();
    $('#addMoneyButton').show();
    $('#betAmountRow').show();
    $('#betRow').show();
    $('#otherButtonRow').show();
    $('#dealCardButton').show(); // button vs buttonRow
}

function cashout() {
    clearCards();
    clearBet();
    clearMessages();
    $('#messageDiv').append("Thank you for playing!");
};

// uses promise to run after dealerHitOrStay finishes
function dealerHitOrStaySuper() {

    dealerHitOrStay().then(hitOrStay => {
        if (hitOrStay == "hit") {
            // will add card and calculate new total
            hitSuper('#dealerCardsDiv', dealerCardArray, '#dealerTotal')
            .then(setTimeout(dealerHitOrStayAgain, 3000)); // try and pause so calc can catch up
        } else {
            determineWinner().then(winnerDetermined).catch(displayError);
        }
    })
};

const dealerHitOrStayAgain = (resolvedArray) => {
    let dealerCount = document.getElementById('dealerTotal').innerText;
    dealerCount = parseInt(dealerCount);
    if ( dealerCount < 17 ){
        hitSuper('#dealerCardsDiv', dealerCardArray, '#dealerTotal').then(dealerHitOrStayAgain);
    } else {
        determineWinner().then(winnerDetermined).catch(displayError);
    }
};

const dealerHitOrStay = () => {
    // send JSON out to server with the dealer cardArray

    return new Promise((resolve, reject) => {
        $.ajax({
            method: 'GET',
            url: 'http://localhost:8080/blackjack/api/dealerVal/' + dealerTotal,
            datatype: 'text',
            success: function (hitOrStay, textStatus, jqXHR) {
                resolve(hitOrStay);
            },
            error: function (request, error) {
                // alert("failed to run hitOrStay function for the dealer");
                reject(error);
            },
        })
    });
}



// send dealercardarray and playercardarray to server and determine winner, receive object with message and multiplier for winnings (1.5x for blackjack)
function determineWinner() {

    let allCards = {
        dealer: dealerCardArray,
        player: playerCardArray
    };

    return new Promise((resolve, reject) => {
        $.ajax({
            method: 'POST',
            url: 'http://localhost:8080/blackjack/api/determineWinner/',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(allCards),
            datatype: 'application/x-www-form-urlencoded;charset=UTF-8',
            // I think message should be an array with the name of the winner and the multiplier for win/loss
            success: function (resultArray, textStatus, jqXHR) {
                resolve(resultArray);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                reject(errorThrown);
            }
        })
    }
    )
}

const winnerDetermined = (resolvedArray) => {
    adjustUserMoney(resolvedArray[2]);
    displayResult(resolvedArray[1]);
    toggleHitButtons();
    showStartButtons();
    // resetHand();
};

function adjustUserMoney(multiplier) {
    userMoney = parseFloat(userMoney) + (betMoney * parseFloat(multiplier));
    userMoney = ((userMoney * 100) / 100).toFixed(2);
    $('#totalUserMoney').empty();
    $('#totalUserMoney').append("Total $: " + userMoney + '<br/>');
};

function displayResult(gameOutcomeMessage){
    $('#messageDiv').css({ "background-color": "gold", "color": "black" });
    $('#messageDiv').append(gameOutcomeMessage);
};

function resetHand(){
    clearBet();
    clearMessages();
    clearCards();
}

function toggleHitButtons(){
    $('#hitButton').toggle();
    $('#stayButton').toggle();
    $('#doubleDownButton').toggle();
    $('#splitButton').toggle();
}

const displayError = (rejectedMessage) => {
    $('#messageDiv').append(rejectedMessage);
}

// what a card looks like from the Deck Of Cards API
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