package de.bcb.security

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.*


fun creKeyPair(algorithm: String = "RSA", keySize: Int = 1024): KeyPair {
    val gen = KeyPairGenerator.getInstance(algorithm)
    gen.initialize(keySize, SecureRandom())
    return gen.genKeyPair()
}

fun main(args: Array<String>) {
    val charset = StandardCharsets.UTF_8

    /*
    val pair = creKeyPair()

    val sig = BcbSignature(
            BcbPublicKey(pair.public),
            BcbPrivateKey(pair.private))
    val enc = BcbEncryption(
            BcbPublicKey(pair.public),
            BcbPrivateKey(pair.private)
    )
    val s = "What"



    val sign = sig.sign(s)//String(Base64.getEncoder().encode(sign(s.toByteArray(charset), pair.private)), charset)
    val verify = sig.verifySignature(s, sign)//verify(s.toByteArray(charset), Base64.getDecoder().decode(sign.toByteArray(charset)), pair.public)
    val encrypt = enc.encrypt(s)//String(Base64.getEncoder().encode(encrypt(s.toByteArray(charset), pair.public)), charset)
    val decrypt = enc.decrypt(encrypt)//String(decrypt(Base64.getDecoder().decode(encrypt.toByteArray(charset)), pair.private), charset)

    System.out.println(pair.toString())
    System.out.println(s)
    System.out.println(sign)
    System.out.println(verify)
    System.out.println(encrypt)
    System.out.println(decrypt)
    */

    for(i in 1..1) {
        val pair = creKeyPair()

        val publicKey = BcbPublicKey(pair.public)
        val privateKey = BcbPrivateKey(pair.private)

        val p = Paths.get("$i")

        Files.createDirectory(p)

        Files.write(p.resolve("public.key"), listOf(publicKey.toString(charset)))
        Files.write(p.resolve("private.key"), listOf(privateKey.toString(charset)))
    }
}