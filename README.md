# SimpleRSA_Cryptography
This is a very simple cryptography system that basically uses the fact that big semiprimes (natural numbers with exactly two prime factors) are computationally hard to factor out.

Here is basically how it works:
1. Pick two prime numbers `p` and `q` that are very big and close in size and a third number *n* = p×q (which is their product). For example, it is _NOT_ a good idea to pick `p = 7` and `q = 199933` and `n = 7 * 199933 = 1399531` because 7 is a small factor and once the third party tries divisibility by 2,3,5,7 it will know the first factor and then trivially deduce the next by dividing n/7. We will assume that `n` is "publicly" visible information.
2. Denote a function `φ(n)` (Euler's totient function) which returns the number of positive integers up to `n` that are coprime with `n` (that means that their greatest common factor is 1). Since `p, q` are primes it follows that `φ(n)=(p-1)(q-1)` (if you want [Proof](https://www.mathsisfun.com/numbers/euler-totient.html) ).
3. Pick a natural number `e`. (People usually pick `e = 65537` because it is big enough for security and in binary it is `10000000000000001` which significantly increases performance for the next steps of encryption and decryption by the user - basically an established balance between security and performance). But in theory you can choose any `e` as long as it is coprime to `φ(n)`. We will assume that `e` is "publicly" visible information as `n`.
4. Calculate a natural number `d` modulo `φ(n)` such that `e*d ≡ 1 (mod φ(n))`. The EEA (Extended Euclidean Algorithm) proves the uniqueness and existence of `d` (no matter the values of `e and φ(n))` and its computational ease. The operation that calculates `d` is commonly refered to as "Modular multiplicative inverse". This is basically the set up.
5. TO ENCRYPT: Let `m` be a natural number representing your message. (Convert the String type to bytes array for example). Calculate `m^e mod n`. The result is the encrypted message let that resulting number be `c`. (NOTE: `m` MUST BE SMALLER THAN `n` - otherwise it won't work because `m>n (mod n)` will give a number in the range `[0, n)` anyways)
6. TO DECRYPT: Take the encrypted message `c` and similarly to step 5 calculate `c^d mod n`. This will give you `m` - the original message. (In a moment we will show why that is the case and why it is computationally challenging to reverse it)

How to execute the process? Firstly, a friend wants to send you a secret message. He uses your `e` and `n` (which are public) to calculate the encrypted message `c`. Then he sends you `c` (an intruder may see the message but it is encrypted). Then when you receive `c` you do step 6 and get their original message. 


Before we begin with the proof we will prove a basic property of modular arithmetic.
Property: If `a ≡ b (mod m)` then `a^k ≡ b^k (mod m)` for some non-negative integer `k`, natural number `m` and integers `a`,`b`.
By definition `a ≡ b (mod m)` means that `m` divides the difference `a-b`.
This is equivalent to:
`a-b = k*m` which implies that `a-b ≡ 0 (mod m)` (because the remainder of `k*m = a-b` is `0` when divided by `m`)
Now: `a^k ≡ b^k (mod m)` =>  `a^k-b^k ≡ 0 (mod m)` (note that when `k=0` this congruence is trivially true for all integers `a`, `b` except for `0` because `0^0` is undefined - we get `a^0 - b^0 = 1-1 = 0 ≡ 0 (mod m)`)

We will prove that `a^k - b^k = (a-b) * P(a,b)` (where `P(a,b)` is some non-negative degree polynomial of `a` and `b` with integer coefficients) for EVERY natural number `k` using induction (we have already proven the statement for `k=0` and that is why we only consider the natural numbers).

1. For `k=1` we have `a^1 - b^1 = a-b`. Therefore `a^1-b^1 = (a-b)*1` and `P(a,b) = 1`.
2. If `a^k - b^k = (a-b) * P(a,b)` holds true (inductive hypothesis). Then `a^(k+1) - b^(k+1) = a(a^k) - b(b^k) = a(a^k) - a(b^k) + a(b^k) - b(b^k)` (in the last step we just add and subtract a(b^k) which equals out) ` = a(a^k - b^k) + (a-b)b^k`. The first mononomial by the inductive hypothesis is `a * (a-b) * P(a,b)` and the second one `(a-b)b^k` both share a common factor `(a-b)` therefore `a(a^k - b^k) + (a-b)b^k = (a-b) * (a * P(a,b) - b^k)` thus the new polynomial `P(a1,b1) = a * P(a,b) - b^k` which indeed has integer coefficients.

We have shown that `S(1)` is true (`S` is the statement) and that if `S(k)` is true then `S(k+1)` is also true. Therefore S(2) is true (because S(1) -> S(2) by the second argument and by the first agrument we have shown that S(1) is true). And by the same recursion S(3) is also true, S(4), S(5) and so on... for all natural numbers.
This completes the proof of the basic property.


Now let's get to the actual proof.
Proof (of step 6 that `c^d ≡ m (mod n)` or in other words that the decryption actually works):
There are two cases:


`Case 1`:
This is the most commom (basically every) case where `m` is NOT divisible by `p` and `q`.

The `gcd(m,n) = 1 and m<n` therefore we can use the Euler's Theorem (See proof [here](https://en.wikipedia.org/wiki/Euler%27s_theorem#Proofs)):
`m^φ(n) ≡ 1 (mod n)` (use exponentiation `k` to both sides where `k` is a non-negative integer - already poven basic property) to get:
`(m^φ(n))^k ≡ 1^k (mod n)` (now multiply both sides by `m` - another basic property of modular arithmetic: `if a ≡ b (mod m) then ak ≡ bk (mod m)` for natural number `k`,`m`. By definition and given condition we get `a-b ≡ 0 (mod m)` which means there exists integer `t` such that `a-b = t * m` now multiply both sides by `k` to get `a*k - b*k = t*m*k = m * (t*k)` therefore m divides the difference `a*k - b*k` thus `ak ≡ bk (mod m)` by the definiton of modulo) to get:
`m*(m^φ(n))^k ≡ m * 1^k (mod n)`
`m^(1+k*φ(n)) ≡ m * 1^k (mod n)`

But by definition in step 4: `e*d ≡ 1 (mod φ(n))` which implies `e*d-1=k*φ(n)` => `e*d = 1+k*φ(n)`. Substitute `e*d` to get:

`m^(e*d) ≡ m * 1^k (mod n)` (`1^k` equals `1` for every `k` and `m^(e*d) = (m^e)^d`)
`(m^e)^d ≡ m (mod n)` but `m^e mod n` is out encrypted message and when we use exponentiation `d` we get our original message `m mod n` which completes `case 1`.


Case 2:
This scenario is very unlikely though it can happen that `m` is divisible by `p` or `q`:
Without any loss of generality let m ≡ 0 (mod p) then using the basic property that we have proven - " If `a ≡ b (mod m)` then `a^k ≡ b^k (mod m)` " for `k = e*d` we get:
`m^(e*d) ≡ 0^(e*d) (mod p)` (of course e*d ≠ 0 cuz 0^0 is undefined)
`m^(e*d) ≡ 0 (mod p)` and we have `m ≡ 0 (mod p)` therefore both are divisible by `p` => `m^(e*d) ≡ m ≡ 0 (mod p)`
Since `m < n = p*q` and `m ≡ 0 (mod p)` it follows that `m` is NOT divisible by `q` (the other prime). Now let's use the Euler's theorem (again):
`m^φ(q) ≡ 1 (mod q)`
`m^(q-1) ≡ 1 (mod q)` (see step 2 of algoritm)
And again from `case 1` we know that `e*d = 1 + k * φ(n) = 1 + k * (p-1) * (q-1)`
Similarly to `case 1`:
`m^(e*d) = m^(1+k*φ(n)) = m^(1+k*(p-1)*(q-1)) = m*(m^(p-1)*(q-1))^k` but since `(p-1)*(q-1)` is a multiple of `q-1` it follows that:
`m^(p-1)(q-1) ≡ 1 (mod q)`
So like in `case 1` we get:
`m^(e*d) ≡ m*1^k = m (mod q)`

Now we know that `m^(e*d) ≡ m (mod p)` and `m^(e*d) ≡ m (mod q)`.
Using the CRT (Chinese remainder theorem - see [Proof](https://crypto.stanford.edu/pbc/notes/numbertheory/crt.html)) we get:
`m^(e*d) ≡ m (mod p*q = n)` which completes `case 2` and the whole proof.
