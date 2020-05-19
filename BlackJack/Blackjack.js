
var userMoney;
var betMoney;
var dealerTotal;
var playerTotal;
var deckId;
var userBusted;;
var dealerCardArray = [];
var playerCardArray = [];


// 2) enable double down & stay buttons. How would I prevent the users from being able to manually revert this?
// 3) disable hit, stay, DD, split buttons once user stays
// 4) Consider moving buttons on left of screen to top center
// 5) move bet buttons to a bootstrap modal
// 6) transition card images better
// 7) learn how to do unit tests in Javascript --> Jest
// 8) getting cards to overlap would be nice so it doesn't go to a new line at 4 or more cards
// would count number of cards in array, then space them out to fit (if #cards > 3)
// 9) I feel like all adjustUserMoney info should go to the server to be hidden and less hackable

// Once a user hits "stay" the "hit", "double down" and "split" buttons should toggle to disabled
// add button to get count for card counting practice

$(document).ready(function () {
    // reset all values
    userMoney = 0.00;
    userMoney = parseFloat(userMoney);
    betMoney = 0.00;
    dealerTotal = 0;
    playerTotal = 0;
    deckId;
    userBusted = false;
    dealerCardArray = [];
    playerCardArray = [];
    clearCards;
    $('#dealerTotal').hide();
});

createAndShuffleDeck();

function addBet(betAmount) {
    clearMessages();
    betAmount = parseFloat(betAmount);
    userMoney = parseFloat(userMoney);

    if (betAmount > userMoney) {
        $('#messageDiv').css({ "background-color": "gold", "color": "black" });
        $('#messageDiv').append("You don't have that much money. Please try again.");
        $('#betAmount').val('');
        betMoney = 0;
    } else {
        $('#betAmount').val('')
        betMoney += betAmount;
        $('#betAmount').val(betMoney);
    }
};

// consider using Bootstrap Modal instead of JS prompt
function addMoney() {
    $('#totalUserMoney').empty();
    isNumber = false;

    while (!isNumber) {
        // how to get cancel to work here
        var addedMoney = prompt("How much money would you like to add?", "added amount");

        if (addedMoney !== null) {
            addedMoney = parseFloat(addedMoney);

            if (isNaN(addedMoney) || addedMoney < 0) {
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
        } else {
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
    $('#dealerTotal').hide();
    dealerTotal = 0;
    playerTotal = 0;
    dealerCardArray = [];
    playerCardArray = [];
    userBusted = false;
};

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
    let currentBet = betMoney;
    if (betMoney >= 1) {
        $.ajax({
            type: 'GET',
            url: 'https://deckofcardsapi.com/api/deck/' + deckId + '/draw/?count=4',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            datatype: 'json',
            success: async function (cardArray) {
                betMoney = currentBet;
                playerLocation = '#playerCardsDiv';
                toggleStartButtons();
                showHitButtons();

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

                playerTotal = await calculateTotal(playerCardArray, '#playerTotal')
                dealerTotal = await calculateTotal(dealerCardArray, '#dealerTotal');
                if (playerTotal == 21) {
                    showImage(card3img, '#dealerCardsDiv', 136, 188);
                    let resultArray = await determineWinner();
                    winnerDetermined(resultArray);
                    // .catch(displayError);
                    // will need to disable hit, stay, DD, split buttons here
                }
                else {
                    showImage('bicycle-back.png', '#dealerCardsDiv', 136, 188);
                }
                showImage(card1img, '#playerCardsDiv', 136, 188);
                showImage(card2img, '#playerCardsDiv', 136, 188);
                showImage(card4img, '#dealerCardsDiv', 136, 188);
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

async function hitSuper(cardLocation, cardArray, location) {
    try {
        let returnedCardArray = await hit(cardLocation, cardArray);
        let cardValue = await calculateTotal(returnedCardArray, location);
    } catch (error) {
        console.log(error);
    }
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
    return new Promise((resolve, reject) => {
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
                }
                resolve(cardValue);
                cardValue = 0;
            },
            error: function () {
                alert("failed to connect to blackjack server");
            }
        })
    })
};

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
        success: function (userBusted) {
            if (userBusted && player == 'Player') {
                // show hidden dealer card if user busts
                $('#dealerCardsDiv').empty();
                showImage(card3img, '#dealerCardsDiv', 136, 188);
                showImage(card4img, '#dealerCardsDiv', 136, 188);
                winnerDetermined(["dealer", "You busted with " + playerTotal, -1.0]);
            }
        },
        error: function () {
            alert("failed to validate count");
        }
    })
};

function toggleStartButtons() {
    $('#cashoutButton').toggle();
    $('#addMoneyButton').toggle();
    $('#betAmountRow').toggle();
    $('#betRow').toggle();
    $('#dealCardsButtonRow').toggle();
    $('#dealCardsButton').toggle();
}

function cashout() {
    clearCards();
    clearBet();
    clearMessages();
    $('#messageDiv').append("Thank you for playing!");
};

async function dealerHitOrStaySuper() {
    $('#dealerCardsDiv').empty();
    for (let i = 0; i < dealerCardArray.length; i++) {
        showImage(dealerCardArray[i].image, '#dealerCardsDiv', 136, 188);
    };
    // showImage(dealerCardArray[0], '#dealerCardsDiv', 136, 188);
    // showImage(card4img, '#dealerCardsDiv', 136, 188);
    $('#dealerTotal').show();

    try {
        let hitOrStay = await dealerHitOrStay();
        if (hitOrStay === "hit") {
            hitSuper('#dealerCardsDiv', dealerCardArray, '#dealerTotal');
            setTimeout(dealerHitOrStaySuper, 1000); // try and pause so calc can catch up
        } else {
            let resultArray = await determineWinner();
            winnerDetermined(resultArray);
        }
    } catch (error) {
        console.log(error);
    }
};

const dealerHitOrStay = () => {
    return new Promise((resolve, reject) => {
        $.ajax({
            method: 'POST',
            url: 'http://localhost:8080/blackjack/api/dealerHitOrStay/',
            headers: {
                'Content-Type': 'application/json'
            },
            data: JSON.stringify(dealerCardArray),
            datatype: 'application/x-www-form-urlencoded;charset=UTF-8',
            success: function (hitOrStay, textStatus, jqXHR) {
                resolve(hitOrStay);
            },
            error: function (request, error) {
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
    toggleStartButtons();
    betMoney = 0;
};

function adjustUserMoney(multiplier) {
    userMoney = parseFloat(userMoney) + (betMoney * parseFloat(multiplier));
    userMoney = ((userMoney * 100) / 100).toFixed(2);
    $('#totalUserMoney').empty();
    $('#totalUserMoney').append("Total $: " + userMoney + '<br/>');
};

function displayResult(gameOutcomeMessage) {
    $('#messageDiv').css({ "background-color": "gold", "color": "black" });
    $('#messageDiv').append(gameOutcomeMessage);
};

function resetHand() {
    clearBet();
    clearMessages();
    clearCards();
}

function showHitButtons() {
    $('#hitButton').show();
    $('#stayButton').show();
    $('#doubleDownButton').show();
    $('#splitButton').show();
}

function toggleHitButtons() {
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