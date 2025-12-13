const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/**
 * Função acionada por HTTP que envia uma notificação para o tópico "risks".
 * Esta função é protegida e só pode ser chamada por usuários autenticados.
 */
exports.sendRiskNotification = functions.https.onRequest(async (req, res) => {
  console.log("Verificando autenticação do usuário.");

  // 1. Extrai o ID Token do cabeçalho de autorização.
  // O formato esperado é: "Authorization: Bearer <ID_TOKEN>"
  if (!req.headers.authorization || !req.headers.authorization.startsWith('Bearer ')) {
    console.error('Nenhum token de autenticação fornecido ou formato inválido.');
    res.status(403).send('Unauthorized');
    return;
  }

  const idToken = req.headers.authorization.split('Bearer ')[1];

  try {
    // 2. Verifica o ID Token usando o Admin SDK.
    // Se o token for inválido, a função lançará um erro.
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    console.log('ID Token verificado com sucesso para o usuário:', decodedToken.uid);

    // 3. Se a verificação for bem-sucedida, o usuário está autenticado.
    // Prossiga com a lógica original da função.
    // Extrai o título e o corpo do corpo da requisição.
    const notificationTitle = req.body.title || "Alerta de Risco!";
    const notificationBody = req.body.body || "Um novo risco de alto impacto foi identificado no sistema.";
    const topic = req.body.topic || "risks"; // Permite que o tópico seja dinâmico, com fallback.

    const payload = {
      notification: {
        title: notificationTitle,
        body: notificationBody,
      },
      data: {
        riskId: "12345",
        level: "critical"
      }
    };

    const response = await admin.messaging().sendToTopic(topic, payload); // Usa o tópico dinâmico
    console.log("Notificação enviada com sucesso:", response);
    res.status(200).send("Notificação enviada com sucesso!");

  } catch (error) {
    console.error('Erro ao verificar o token de autenticação:', error);
    res.status(403).send('Unauthorized');
  }
});